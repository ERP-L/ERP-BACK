package com.app.erp.organizations.domain;

import com.app.erp.organizations.domain.valueobjects.BranchName;
import com.app.erp.organizations.domain.valueobjects.AddressLine;
import com.app.erp.organizations.domain.valueobjects.UbigeoId;
import com.app.erp.organizations.domain.exception.AlreadyActiveException;
import com.app.erp.organizations.domain.exception.AlreadyInactiveException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public final class Branch {
    private Integer branchId;
    private final int companyId;
    private BranchName name;
    private AddressLine address;
    private UbigeoId ubigeoId;
    private boolean isActive;
    private OffsetDateTime createdUtc;
    private OffsetDateTime updatedUtc;

    private Branch(Integer branchId,
                   int companyId,
                   BranchName name,
                   AddressLine address,
                   UbigeoId ubigeoId,
                   boolean isActive,
                   OffsetDateTime createdUtc,
                   OffsetDateTime updatedUtc) {
        this.branchId = branchId;
        this.companyId = companyId;
        this.name = Objects.requireNonNull(name, "name");
        this.address = address; // opcional
        this.ubigeoId = Objects.requireNonNull(ubigeoId, "ubigeoId");
        this.isActive = isActive;
        this.createdUtc = createdUtc;
        this.updatedUtc = updatedUtc;
    }

    /** Factory para crear una Branch válida a nivel de dominio. */
    public static Branch register(int companyId, BranchName name, AddressLine address, UbigeoId ubigeoId) {
        // created/updated pueden ser null si la BD los calculará; si prefieres, usa OffsetDateTime.now(UTC)
        return new Branch(
                null, companyId, name, address, ubigeoId,
                true, null, null
        );
    }

    public static Branch fromPersistence(
            Integer branchId,
            int companyId,
            BranchName name,
            AddressLine address,
            UbigeoId ubigeoId,
            boolean isActive,
            OffsetDateTime createdUtc,
            OffsetDateTime updatedUtc
    ) {
        return new Branch(branchId, companyId, name, address, ubigeoId, isActive, createdUtc, updatedUtc);
    }

    public void rename(BranchName newName) {
        this.name = Objects.requireNonNull(newName, "newName");
        touch();
    }

    public void move(AddressLine newAddress, UbigeoId newUbigeoId) {
        this.address = newAddress; // puede ser null
        this.ubigeoId = Objects.requireNonNull(newUbigeoId, "newUbigeoId");
        touch();
    }

    public void activate() {
        if (this.isActive) throw new AlreadyActiveException("Branch ya estaba activa");
        this.isActive = true;
        touch();
    }

    public void deactivate() {
        if (!this.isActive) throw new AlreadyInactiveException("Branch ya estaba inactiva");
        this.isActive = false;
        touch();
    }

    private void touch() {
        this.updatedUtc = OffsetDateTime.now(ZoneOffset.UTC);
    }

    // getters (sin setters públicos para mantener invariantes)
    public Integer getBranchId() { return branchId; }
    public int getCompanyId() { return companyId; }
    public BranchName getName() { return name; }
    public AddressLine getAddress() { return address; }
    public UbigeoId getUbigeoId() { return ubigeoId; }
    public boolean isActive() { return isActive; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public OffsetDateTime getUpdatedUtc() { return updatedUtc; }

    // setters de persistencia controlados (package-private) si necesitas hidratar desde infra
    void setBranchId(Integer branchId) { this.branchId = branchId; }
    void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    void setUpdatedUtc(OffsetDateTime updatedUtc) { this.updatedUtc = updatedUtc; }
}


