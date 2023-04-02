package ic.unicamp.fb.block;

import ic.unicamp.fb.block.utils.TempBMDirectoryUtil;

public class TempGitVCS extends GitVCS {

    @Override
    public String getCurrentDirectory() {
        return TempBMDirectoryUtil.getBMDirectoryAsPath().toString();
    }
}
