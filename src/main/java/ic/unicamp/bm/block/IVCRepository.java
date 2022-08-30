package ic.unicamp.bm.block;

import java.nio.file.Path;

public interface IVCRepository {

  Path upsertRepository(String repositoryName);
  void updateOutDirectory(String path);
  void getOutDirectory();
}
