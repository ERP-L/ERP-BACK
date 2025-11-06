package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.commands.CreateProductCommand;
import com.app.erp.inventory.application.dtos.results.CreateProductResult;
import com.app.erp.inventory.application.port.ProductWritePort;
import com.app.erp.inventory.application.internal.security.InventoryAuthorizationPolicy;
import com.app.erp.shared.exceptions.InvalidInputException;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CreateProductHandler {

    private final InventoryAuthorizationPolicy policy;
    private final ProductWritePort writePort;

    public CreateProductHandler(InventoryAuthorizationPolicy policy,
                                ProductWritePort writePort) {
        this.policy = policy;
        this.writePort = writePort;
    }

    public CreateProductResult handle(CreateProductCommand cmd, AuthContext auth) {
        // 1) Validaciones de forma mínimas
        if (cmd.getProductName() == null || cmd.getProductName().trim().isEmpty()) {
            throw new InvalidInputException("productName es requerido.");
        }
        if (cmd.getUomId() == null) {
            throw new InvalidInputException("uomId es requerido.");
        }

        // 2) Defaults de aplicación (NO se piden al cliente; la app los impone)
        cmd.setCompanyId(auth.companyId());           // del token
        cmd.setStatus(1);                             // default fijo
        cmd.setCreatedUtc(Instant.now());            // UTC del servidor

        // Normaliza booleans null → false
        if (cmd.getIsSerialized() == null) cmd.setIsSerialized(false);
        if (cmd.getIsBatchControlled() == null) cmd.setIsBatchControlled(false);

        // Regla rápida (también la aplica el SP, pero ahorramos round-trip)
        if (Boolean.TRUE.equals(cmd.getIsSerialized()) && Boolean.TRUE.equals(cmd.getIsBatchControlled())) {
            throw new InvalidInputException("Un producto no puede ser a la vez serializado y controlado por lote.");
        }

        // 3) Autorización y pertenencia de CategoryID
        policy.checkCreateProduct(cmd, auth);

        // 4) Persistencia vía SP
        return writePort.createProduct(cmd);
    }
}
