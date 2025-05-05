public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", type, value);
    }

    // Проверки типов токенов
    public boolean isOperand() {
        return type == TokenType.OPERAND;
    }

    public boolean isOperator() {
        return type == TokenType.OPERATOR;
    }

    public boolean isLeftParenthesis() {
        return type == TokenType.LEFT_PARENTHESIS;
    }

    public boolean isRightParenthesis() {
        return type == TokenType.RIGHT_PARENTHESIS;
    }

    public boolean isControlStructure() {
        return type == TokenType.CONTROL_STRUCTURE;
    }

    // Получение приоритета для операторов
    public int getPrecedence() {
        if (type == TokenType.OPERATOR) {
            switch (value) {
                case "+":
                case "-":
                    return 1;
                case "*":
                case "/":
                    return 2;
                default:
                    return 0;
            }
        }
        return 0;  // Если не оператор
    }
}