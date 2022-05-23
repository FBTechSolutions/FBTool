package ic.unicamp.bm;


import ic.unicamp.bm.cli.Cmd;
import picocli.CommandLine;

public class App {

  public static void main(String[] args) {
    new CommandLine(new Cmd()).execute(args);
  }
}
