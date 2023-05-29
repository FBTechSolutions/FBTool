package ic.unicamp.fb.block;

public class GitVCSManager {

    private static IVCSAPI instance = null;
    private static IVCSAPI temporal = null;
    private static IVCRepository repository = null;

    public static IVCSAPI createInstance() {
        if (instance == null) {
            instance = new GitVCS();
        }
        return instance;
    }

    public static IVCSAPI createTemporalGitBlockInstance() {
        if (temporal == null) {
            temporal = new TempGitVCS();
        }
        return temporal;
    }

    public static IVCRepository createGitRepositoryInstance() {
        if (repository == null) {
            repository = new GitRepository();
        }
        return repository;
    }

}
