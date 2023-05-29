package ic.unicamp.fb.block;

import ic.unicamp.fb.block.utils.TempFBDirectoryUtil;

public class TempGitVCS extends GitVCS {

    @Override
    public String getCurrentDirectory() {
        return TempFBDirectoryUtil.getFBDirectoryAsPath().toString();
    }
}
