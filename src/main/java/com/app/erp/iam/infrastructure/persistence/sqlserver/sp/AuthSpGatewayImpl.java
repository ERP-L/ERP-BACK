package com.app.erp.iam.infrastructure.persistence.sqlserver.sp;

import com.app.erp.iam.application.internal.outboundservices.hashing.HashingService;
import com.app.erp.iam.application.internal.outboundservices.tokens.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.app.erp.iam.application.internal.outboundservices.auth.AuthGateway;
import com.app.erp.iam.application.dto.commands.LoginCommand;
import com.app.erp.iam.application.dto.commands.RegisterCommand;
import com.app.erp.iam.application.dto.results.LoginResult;
import com.app.erp.iam.application.dto.results.RegisterResult;
import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class AuthSpGatewayImpl implements AuthGateway {

    private final DataSource dataSource;
    private final String passwordAlgorithm;

    private final HashingService hashingService;
    private final TokenService tokenService;
    private final long jwtTtlSeconds;

    public AuthSpGatewayImpl(DataSource dataSource,
                             @Value("${iam.password.algorithm:bcrypt}") String passwordAlgorithm,
                             HashingService hashingService, TokenService tokenService,
                             @Value("${security.jwt.expiration-seconds:1800}") long jwtTtlSeconds
                             ) {
        this.dataSource = dataSource;
        this.passwordAlgorithm = passwordAlgorithm;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.jwtTtlSeconds = jwtTtlSeconds;
    }

    @Override
    public RegisterResult register(RegisterCommand cmd) {
        // LLAMAMOS AL NUEVO SP:
        final String call =
                "{call security.sp_RegisterSuperAdmin(" +
                        // 1..5
                        "?, ?, ?, ?, ?," +
                        // 6..11
                        "?, ?, ?, ?, ?, ?," +
                        // 12..19
                        "?, ?, ?, ?, ?, ?, ?, ?," +
                        // 20..21
                        "?, ?," +
                        // 22  <-- FALTA ESTE EN TU VERSION
                        "?," +
                        // 23..25 OUTS
                        "?, ?, ?)}";         // OUT: OutCompanyID, OutSecurityUserID, OutAuthUserID

        try (Connection cn = dataSource.getConnection();
             CallableStatement cs = cn.prepareCall(call)) {

            // 1..21 (igual que ya tienes)
            cs.setString(1,  cmd.getSecurityUser().getUsername());
            cs.setString(2,  cmd.getSecurityUser().getEmail());

            // hash como bytes (si tu columna es VARBINARY de hash crudo)
            byte[] hashBytes = cmd.getSecurityUser().getEncodedPassword().getBytes(StandardCharsets.UTF_8);
            cs.setBytes(3, hashBytes);

            cs.setString(4, this.passwordAlgorithm);
            cs.setNull(5, Types.VARBINARY);

            cs.setString(6,  cmd.getTenantUser().getFirstName());
            cs.setString(7,  cmd.getTenantUser().getLastName());
            cs.setString(8,  cmd.getTenantUser().getGender());
            cs.setString(9,  cmd.getTenantUser().getPhone());

            // cuidado con nulls:
            if (cmd.getTenantUser().getDocumentTypeId() == null) cs.setNull(10, Types.INTEGER);
            else cs.setInt(10, cmd.getTenantUser().getDocumentTypeId());

            cs.setString(11, cmd.getTenantUser().getDocumentNumber());

            cs.setString(12, cmd.getCompany().getLegalName());

            if (cmd.getCompany().getDocumentTypeId() == null) cs.setNull(13, Types.INTEGER);
            else cs.setInt(13, cmd.getCompany().getDocumentTypeId()); // el SP lo fuerza a 3

            cs.setString(14, cmd.getCompany().getDocumentNumber());
            cs.setString(15, cmd.getCompany().getTradeName());
            cs.setString(16, cmd.getCompany().getAddress());

            if (cmd.getCompany().getUbigeoId() == null) cs.setNull(17, Types.INTEGER);
            else cs.setInt(17, cmd.getCompany().getUbigeoId());

            cs.setString(18, cmd.getCompany().getPhone());
            cs.setString(19, cmd.getCompany().getEmail());

            cs.setInt(20, 1); // SecurityDefaultRoleId
            cs.setInt(21, 1); // AuthDefaultRoleId

            // 22) AuthFallbackRoleName (IN). Puedes enviar null o "OWNER"
            cs.setString(22, "OWNER"); // o cs.setNull(22, Types.NVARCHAR);

            // OUTS en 23..25
            cs.registerOutParameter(23, Types.INTEGER); // OutCompanyID
            cs.registerOutParameter(24, Types.INTEGER); // OutSecurityUserID
            cs.registerOutParameter(25, Types.INTEGER); // OutAuthUserID

            cs.execute();

            Integer companyId      = getIntOrNull(cs, 23);
            Integer securityUserId = getIntOrNull(cs, 24);
            Integer authUserId     = getIntOrNull(cs, 25);

            return new RegisterResult(companyId, securityUserId, authUserId);
        } catch (SQLException e) {
            // Aquí podrías analizar e.getMessage() para mapear a tus códigos (USERNAME_ALREADY_EXISTS, etc.)
            throw new RuntimeException("SQL_ERROR: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginResult login(LoginCommand cmd) {
        // 1) Obtener datos base por email
        final String sqlGet = "{call security.sp_GetUserForLogin(?)}";

        Integer securityUserId = null;
        boolean isActive = false;
        byte[] passwordHashBytes = null;

        try (var con = dataSource.getConnection();
             var cs = con.prepareCall(sqlGet)) {

            cs.setString(1, cmd.email());
            try (var rs = cs.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Credenciales inválidas");
                }
                securityUserId   = rs.getInt("SecurityUserID");
                isActive         = rs.getBoolean("IsActive");
                passwordHashBytes= rs.getBytes("PasswordHash");
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error in sp_GetUserForLogin", e);
        }

        if (securityUserId == null || !isActive) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 2) Comparar contraseña
        // Si guardas bcrypt/argon2 como texto en VARBINARY, son bytes del string:
        String storedHash = new String(passwordHashBytes, java.nio.charset.StandardCharsets.UTF_8);
        if (!hashingService.matches(cmd.password(), storedHash)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3) Construir sesión y roles
        final String sqlBuild = "{call security.sp_BuildSessionAfterLogin(?, ?, ?, ?)}";
        Integer authUserId = null;
        Integer companyId  = null;
        var globalRoles  = new java.util.ArrayList<Integer>();
        var companyRoles = new java.util.ArrayList<Integer>();

        try (var con = dataSource.getConnection();
             var cs = con.prepareCall(sqlBuild)) {

            cs.setInt(1, securityUserId);
            cs.setString(2, cmd.ipAddress());
            cs.setString(3, cmd.device());
            cs.setString(4, cmd.userAgent());

            boolean hasRs = cs.execute();

            // RS #1: base (ids)
            if (hasRs) try (var rs1 = cs.getResultSet()) {
                if (rs1.next()) {
                    // SecurityUserID también viene, pero ya lo tenemos
                    authUserId = (Integer) rs1.getObject("AuthUserID");
                    companyId  = (Integer) rs1.getObject("CompanyID");
                }
            }

            // RS #2: roles globales
            if (cs.getMoreResults()) try (var rs2 = cs.getResultSet()) {
                while (rs2.next()) globalRoles.add(rs2.getInt("RoleID"));
            }

            // RS #3: roles por compañía (o vacío consistente)
            if (cs.getMoreResults()) try (var rs3 = cs.getResultSet()) {
                while (rs3.next()) companyRoles.add(rs3.getInt("RoleID"));
            }

        } catch (Exception e) {
            throw new RuntimeException("DB error in sp_BuildSessionAfterLogin", e);
        }

        // 4) Emitir JWT con claims
        var claims = new java.util.HashMap<String, Object>();
        claims.put("sid", securityUserId);
        if (authUserId != null) claims.put("aid", authUserId);
        if (companyId  != null) claims.put("cid", companyId);
        claims.put("roles_global",  globalRoles);
        claims.put("roles_company", companyRoles);

        String subject = cmd.email(); // o String.valueOf(securityUserId)

// Generar token con claims
        String jwt = tokenService.generateToken(subject, claims);

// Si tu LoginResult pide expiresIn, usa el valor de configuración (inyectado)
        return new LoginResult(
                jwt,
                jwtTtlSeconds,
                securityUserId,
                authUserId,
                companyId,
                globalRoles,
                companyRoles
        );
    }


    // Helpers

    private static Integer getIntOrNull(CallableStatement cs, int index) throws SQLException {
        int v = cs.getInt(index);
        return cs.wasNull() ? null : v;
    }

    private static Integer getIntOrNull2(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }


    private static boolean isStatusResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            if ("Status".equalsIgnoreCase(md.getColumnName(i))) return true;
        }
        return false;
    }


    private static Boolean getBooleanOrNull(ResultSet rs, String col) throws SQLException {
        boolean v = rs.getBoolean(col);
        return rs.wasNull() ? null : v;
    }

    private static String safeGetString(ResultSet rs, String col) throws SQLException {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
}
