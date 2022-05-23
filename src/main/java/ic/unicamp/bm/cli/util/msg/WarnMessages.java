package ic.unicamp.bm.cli.util.msg;

public interface WarnMessages {

  String WARN_1__SUBGRAPH_MALFORMED = "Subgraph Malformed";

  String WARN_0__FEATURE_ROOT_ALREADY_EXITS = "Root Feature '%s' already exits in our DB";
  String WARN_1__FEATURE_PARENT_DOES_NOT_EXITS = "Parent Feature '%s' does not exits in our DB";
  String WARN_2__FEATURE_ALREADY_EXITS = "Feature '%s; already exits in our DB";

  String WARN_3__OBJ_DIR_NOT_CREATED = "Object Directory not created";
  String WARN_3__OBJ_DIR_ALREADY_EXITS = "Object Directory already exits";

  String WARN_4__OBJ_DIR_HASH_MAP_FILE_NOT_EXITS = "(Data) Hash Map File does not exits";
  String WARN_4__OBJ_DIR_HASH_MAP_FILE_NOT_CREATED = "(Data) Hash Map File not created";
  String WARN_5__OBJ_DIR_HASH_MAP_FILE_ALREADY_EXITS = "(Data) Hash Map File already exits";

  String WARN_4__OBJ_DIR_GRAPH_FILE_NOT_EXITS = "(Relations) Graph File does not exits";
  String WARN_4__OBJ_DIR_GRAPH_FILE_NOT_CREATED = "(Relations) Graph File not created";
  String WARN_5__OBJ_DIR_GRAPH_FILE_ALREADY_EXITS = "(Relations) Graph File file already exits";

  String WARN_3__WE_COULD_NOT_CREATE_ROOT_FEATURE = "we couldn't create ROOT Feature";

  String WARN_3__WE_COULD_NOT_CREATE_SPLM_DIR_BECAUSE_ALREADY_EXITS =
      "We could not create a new splm directory because there is already a .splm directory";
  String WARN_3__WE_COULD_NOT_CREATE_GIT_DIR_BECAUSE_ALREADY_EXITS =
      "We could not create a new git directory because there is already a .git directory";

  String WARN_4__SPLM_DIR_NOT_CREATED = "splm directory not created";
  String WARN_4__SPLM_DIR_NOT_REMOVED = "splm directory not removed";

  String WARN_4__GIT_DIR_NOT_CREATED = "git directory not created";
  String WARN_4__GIT_DIR_NOT_REMOVED = "git directory not removed";

  String WAR_0__BRANCH_NAME_IS_BEEING_USED_IN_THE_LOCAL_GIT =
      "There is already a GIT Branch with the '%s' name";
  String WAR_0__PARENT_GIT_BRANCH_DOES_NOT_EXITS =
      "Parent GIT Branch with the '%s' name does not exits";
  String WAR_0__GIT_IGNORE_HELLO_SPLM_WAS_NOT_CREATED = "gitignore file was not created";

  String WAR_0__PRODUCT_NAME_ALREADY_EXITS = "product name '%s' already exits";
  String WAR_0__FEATURE_NOT_FOUND = "feature '%s' not found";
  String WAR_0__THERE_IS_NOT_SELECTED_FEATURE_TO_RELATE_WITH_THE_PRODUCT =
      "there is not selected features to relate to the product";


}
