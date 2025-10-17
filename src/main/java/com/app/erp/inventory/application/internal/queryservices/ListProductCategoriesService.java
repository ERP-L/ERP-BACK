package com.app.erp.inventory.application.internal.queryservices;

import com.app.erp.inventory.application.internal.port.ProductCategoryReadPort;
import com.app.erp.inventory.interfaces.rest.contracts.CategoryTreeResponse;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.inventory.application.internal.messages.results.ReparentProductCategoryResult;
import com.app.erp.shared.exceptions.AuthorizationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListProductCategoriesService {

    private final ProductCategoryReadPort readPort;
    public ListProductCategoriesService(ProductCategoryReadPort readPort) {
        this.readPort = readPort;
    }

    public List<CategoryTreeResponse> listAll(AuthContext auth, Boolean onlyActive) {
        if (auth == null || auth.companyId() == null) {
            throw new AuthorizationException("No hay contexto de autenticación o companyId");
        }

        int companyId = auth.companyId();
        boolean onlyActiveFlag = Boolean.TRUE.equals(onlyActive);

    List<ReparentProductCategoryResult> flat = readPort.getAllByCompany(companyId, onlyActiveFlag);

        // Build nodes map
        Map<Integer, CategoryTreeResponse> nodes = new HashMap<>();
        for (ReparentProductCategoryResult r : flat) {
            CategoryTreeResponse node = new CategoryTreeResponse();
            node.setCategoryId(r.getCategoryId());
            node.setCategoryName(r.getCategoryName());
            node.setDescription(r.getDescription());
            node.setParentCategoryId(r.getParentCategoryId());
            node.setIsActive(r.getIsActive());
            node.setCreatedUtc(r.getCreatedUtc());
            node.setCompanyId(r.getCompanyId());
            node.setChildren(new ArrayList<>());
            nodes.put(node.getCategoryId(), node);
        }

        // Attach children to parents
        List<CategoryTreeResponse> roots = new ArrayList<>();
        for (CategoryTreeResponse node : nodes.values()) {
            Integer parentId = node.getParentCategoryId();
            if (parentId == null || !nodes.containsKey(parentId)) {
                roots.add(node);
            } else {
                nodes.get(parentId).getChildren().add(node);
            }
        }

        // Optionally sort children alphabetically by name
        sortRecursively(roots);

        return roots;
    }

    private void sortRecursively(List<CategoryTreeResponse> list) {
        list.sort((a, b) -> {
            if (a.getCategoryName() == null) return -1;
            if (b.getCategoryName() == null) return 1;
            return a.getCategoryName().compareToIgnoreCase(b.getCategoryName());
        });
        for (CategoryTreeResponse n : list) {
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                sortRecursively(n.getChildren());
            }
        }
    }
}
