import java.util.*;

public class RPNConverter {

    private static final Map<String, Integer> OPERATOR_PRIORITY = new HashMap<>() {{
        put("=", 1);
        put("||", 2);
        put("&&", 3);
        put("<", 4); put("<=", 4); put(">", 4); put(">=", 4); put("==", 4); put("!=", 4);
        put("+", 5); put("-", 5);
        put("*", 6); put("/", 6); put("%", 6);
        put("(", 0); put(")", 0);
        put("[", -1); put("]", -1);
    }};

    public List<String> convertToRPN(List<String> codeLines) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < codeLines.size()) {
            String line = codeLines.get(i).trim();

            if (line.equals("{") || line.equals("}")) {
                i++;
                continue;
            }

            if (line.startsWith("if")) {
                String condition = extractCondition(line);
                result.add(String.join(" ", toOpz(tokenize(condition))) + " УПЛ");
                i++;
                continue;
            }

            if (line.startsWith("while")) {
                String condition = extractCondition(line);
                result.add(String.join(" ", toOpz(tokenize(condition))) + " УЦ");
                i++;
                continue;
            }

            if (line.startsWith("for")) {
                String[] parts = extractCondition(line).split(";");
                if (parts.length != 3) {
                    result.add("Ошибка в синтаксисе for");
                    i++;
                    continue;
                }

                result.add(String.join(" ", toOpz(tokenize(parts[0]))));
                result.add(String.join(" ", toOpz(tokenize(parts[1]))) + " УЦ");

                List<String> loopBody = new ArrayList<>();
                i++;
                while (i < codeLines.size() && !codeLines.get(i).trim().equals("}")) {
                    loopBody.add(codeLines.get(i).trim());
                    i++;
                }
                result.addAll(convertToRPN(loopBody));
                result.add(String.join(" ", toOpz(tokenize(parts[2]))));
                result.add("УЦ");

                i++;
                continue;
            }

            if (line.contains("=") && !line.contains("==")) {
                String[] parts = line.split("=", 2);
                String left = parts[0].trim();
                String right = parts[1].trim().replace(";", "");

                List<String> varName = processArrayAccess(left);
                List<String> exprTokens = toOpz(processArrayAccess(right));

                result.add(String.join(" ", varName) + " " + String.join(" ", exprTokens) + " =");
                i++;
                continue;
            }

            if (line.contains("[") && line.contains("]")) {
                result.add(String.join(" ", processArrayAccess(line)));
                i++;
                continue;
            }

            if (line.contains("console.log")) {
                String argsStr = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).replace(";", "");
                List<String> argsTokens = tokenize(argsStr);
                List<String> argsRPN = toOpz(argsTokens);
                result.add(String.join(" ", argsRPN) + " console.log");
                i++;
                continue;
            }

            i++;
        }

        return result;
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        char stringChar = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (inString) {
                current.append(c);
                if (c == stringChar && (current.length() < 2 || current.charAt(current.length() - 2) != '\\')) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    inString = false;
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                inString = true;
                stringChar = c;
                current.append(c);
                continue;
            }

            if (Character.isWhitespace(c)) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            if ("()+-*/%<>=!&|[]".indexOf(c) != -1) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }

                if (i + 1 < expression.length()) {
                    String twoChar = "" + c + expression.charAt(i + 1);
                    if (OPERATOR_PRIORITY.containsKey(twoChar)) {
                        tokens.add(twoChar);
                        i++;
                        continue;
                    }
                }

                tokens.add(String.valueOf(c));
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    private List<String> toOpz(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (token.matches("[a-zA-Z0-9_\"']+")) {
                output.add(token);
            } else if (token.equals("(") || token.equals("[")) {
                stack.push(token);
            } else if (token.equals(")") || token.equals("]")) {
                while (!stack.isEmpty() && !(stack.peek().equals("(") || stack.peek().equals("["))) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty()) stack.pop();
            } else if (OPERATOR_PRIORITY.containsKey(token)) {
                while (!stack.isEmpty() && OPERATOR_PRIORITY.getOrDefault(stack.peek(), 0) >= OPERATOR_PRIORITY.get(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private List<String> processArrayAccess(String expression) {
        List<String> tokens = tokenize(expression);
        List<String> output = new ArrayList<>();
        int i = 0;

        while (i < tokens.size()) {
            if (i < tokens.size() - 2 && tokens.get(i + 1).equals("[")) {
                String arrayName = tokens.get(i);
                String index = tokens.get(i + 2);
                List<String> indexOpz = toOpz(Collections.singletonList(index));
                output.add(arrayName);
                output.addAll(indexOpz);
                output.add("1");
                output.add("АЭМ");
                i += 3;
            } else if (!tokens.get(i).equals("]")) {
                output.add(tokens.get(i));
            }
            i++;
        }

        return output;
    }

    private String extractCondition(String line) {
        return line.substring(line.indexOf('(') + 1, line.lastIndexOf(')'));
    }
}
