import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите путь к JS-файлу: ");
        String filePath = scanner.nextLine();

        try {
            // Чтение строк из JS-файла
            List<String> codeLines = Files.readAllLines(Paths.get(filePath));

            // Инициализация конвертера
            RPNConverter converter = new RPNConverter();
            List<String> rpnLines = converter.convertToRPN(codeLines);

            // Вывод результата
            System.out.println("--- Обратная польская запись (ОПЗ) ---");
            for (String line : rpnLines) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}