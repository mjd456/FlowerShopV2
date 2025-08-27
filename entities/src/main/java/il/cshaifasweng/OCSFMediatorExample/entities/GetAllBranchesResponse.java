package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class GetAllBranchesResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Branch> branches;

    public GetAllBranchesResponse(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Branch> getBranches() {
        return branches;
    }
}
