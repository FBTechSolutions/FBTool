package ic.unicamp.fb.generator;

public interface IGenerator {
    void generateCuts(String filePath);

    void cleanUnnecessaryCuts(String filePath);
}
