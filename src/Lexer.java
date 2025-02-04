import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Lexer {
    private static final LinkedHashMap<Pattern, TokenType> TOKEN_PATTERNS = new LinkedHashMap<>();
    private final List<Token> tokens = new ArrayList<>();

    static {
        // 1️⃣ Сначала ключевые слова, чтобы не спутать их с идентификаторами
        TOKEN_PATTERNS.put(Pattern.compile("\\b(var|let|const|if|else|while|for|return|function)\\b"), TokenType.KEYWORD);
        TOKEN_PATTERNS.put(Pattern.compile("\"[^\"]*\"|'[^']*'"), TokenType.STRING);
        TOKEN_PATTERNS.put(Pattern.compile("\\b\\d+(\\.\\d+)?\\b"), TokenType.NUMBER);
        TOKEN_PATTERNS.put(Pattern.compile("[+\\-*/=<>!&|:.]+"), TokenType.OPERATOR);  // 2️⃣ Теперь `:` распознаётся как оператор
        TOKEN_PATTERNS.put(Pattern.compile("[{}();,]"), TokenType.SEPARATOR);
        TOKEN_PATTERNS.put(Pattern.compile("//.*|/\\*.*?\\*/"), TokenType.COMMENT);
        TOKEN_PATTERNS.put(Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"), TokenType.IDENTIFIER);
    }

    public Lexer(String filePath) throws IOException {
        List<String> lines = readFile(filePath);
        tokenize(lines);
    }

    private List<String> readFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private void tokenize(List<String> lines) {
        for (String line : lines) {
            TreeMap<Integer, Token> foundTokens = new TreeMap<>();
            StringBuffer remainingLine = new StringBuffer(line);

            for (Map.Entry<Pattern, TokenType> entry : TOKEN_PATTERNS.entrySet()) {
                Matcher matcher = entry.getKey().matcher(remainingLine);

                while (matcher.find()) {
                    int start = matcher.start();
                    String value = matcher.group();

                    // Если ключевое слово найдено — удаляем его сразу
                    if (entry.getValue() == TokenType.KEYWORD) {
                        foundTokens.put(start, new Token(entry.getValue(), value));
                        remainingLine.replace(matcher.start(), matcher.end(), " ".repeat(value.length()));
                        matcher = entry.getKey().matcher(remainingLine);
                        continue;
                    }

                    // Исключаем строки из дальнейшего анализа
                    if (entry.getValue() == TokenType.STRING) {
                        foundTokens.put(start, new Token(entry.getValue(), value));
                        remainingLine.replace(matcher.start(), matcher.end(), " ".repeat(value.length()));
                        matcher = entry.getKey().matcher(remainingLine);
                        continue;
                    }

                    foundTokens.put(start, new Token(entry.getValue(), value));
                }
            }

            tokens.addAll(foundTokens.values());
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }
}