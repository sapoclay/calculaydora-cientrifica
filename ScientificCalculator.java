package calcien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Arrays;
import java.util.EmptyStackException;

// Clase principal que hereda de JFrame para crear la interfaz gráfica
public class ScientificCalculator extends JFrame {
    private JTextField display;          // Campo de texto para mostrar la expresión y resultados
    private StringBuilder expression = new StringBuilder();  // Almacena la expresión actual

    // Conjunto de funciones matemáticas soportadas
    private static final Set<String> FUNCTIONS = new HashSet<>(
        Arrays.asList("sin", "cos", "tan", "asin", "acos", "atan", "sqrt")
    );

    // Tooltips para los botones (textos de ayuda al pasar el mouse)
    private static final Map<String, String> TOOLTIPS = new HashMap<>();
    static {
        TOOLTIPS.put("sin", "Seno: calcula sin(x) en grados");
        TOOLTIPS.put("cos", "Coseno: calcula cos(x) en grados");
        TOOLTIPS.put("tan", "Tangente: calcula tan(x) en grados");
        TOOLTIPS.put("asin", "Arcoseno: calcula asin(x) y devuelve grados");
        TOOLTIPS.put("acos", "Arcocoseno: calcula acos(x) y devuelve grados");
        TOOLTIPS.put("atan", "Arcotangente: calcula atan(x) y devuelve grados");
        TOOLTIPS.put("sqrt", "Raíz cuadrada: calcula √x");
        TOOLTIPS.put("+", "Suma");
        TOOLTIPS.put("-", "Resta");
        TOOLTIPS.put("*", "Multiplicación");
        TOOLTIPS.put("/", "División");
        TOOLTIPS.put("^", "Potenciación");
        TOOLTIPS.put("%", "Porcentaje");
        TOOLTIPS.put("(", "Paréntesis izquierdo");
        TOOLTIPS.put(")", "Paréntesis derecho");
        TOOLTIPS.put("=", "Calcula el resultado");
        TOOLTIPS.put("C", "Limpia toda la expresión");
        TOOLTIPS.put("←", "Borrar último carácter");
        TOOLTIPS.put(".", "Punto decimal");
    }

    // Precedencia de operadores para el algoritmo shunting-yard
    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    static {
        PRECEDENCE.put("+", 1);
        PRECEDENCE.put("-", 1);
        PRECEDENCE.put("*", 2);
        PRECEDENCE.put("/", 2);
        PRECEDENCE.put("%", 2);
        PRECEDENCE.put("^", 3);
    }

    // Constructor de la calculadora
    public ScientificCalculator() {
        setTitle("CalculaYDora CientRífica");
        setSize(500, 600);  // Tamaño aumentado para mejor visualización
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centrar en pantalla
        initUI();  // Inicializar interfaz de usuario
    }

    // Inicializa los componentes de la interfaz de usuario
    private void initUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));  // Márgenes internas
        setContentPane(content);

        // Configuración del display
        display = new JTextField();
        display.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBorder(new EmptyBorder(5, 5, 5, 5));
        content.add(display, BorderLayout.NORTH);

        // Panel para los botones con GridBagLayout para mejor control
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);  // Espaciado entre botones

        // Orden de los botones en la interfaz
        String[] buttons = {
            "sin", "cos", "tan", "sqrt", "←",
            "asin", "acos", "atan", "^", "%",
            "7", "8", "9", "/", "(",
            "4", "5", "6", "*", ")",
            "1", "2", "3", "-", "=",
            "0", ".", "+", "C"
        };

        // Creación y disposición de los botones
        int row = 0, col = 0;
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 20));

            // Asignar tooltips si existen
            if (TOOLTIPS.containsKey(text)) {
                btn.setToolTipText(TOOLTIPS.get(text));
            }

            // Estilo especial para el botón de limpiar
            if (text.equals("C")) {
                btn.setBackground(Color.CYAN);
                btn.setForeground(Color.WHITE);
            }

            // Manejador de eventos para los botones
            btn.addActionListener(e -> handleButton(e.getActionCommand()));

            // Configuración de posición en el grid
            gbc.gridx = col;
            gbc.gridy = row;

            // Ajuste especial para el botón "C" que ocupa 2 columnas
            if (text.equals("C")) {
                gbc.gridwidth = 2;
            } else {
                gbc.gridwidth = 1;
            }

            panel.add(btn, gbc);
            col += gbc.gridwidth;
            if (col >= 5) {  // 5 columnas por fila
                col = 0;
                row++;
            }
        }

        content.add(panel, BorderLayout.CENTER);

        // Manejador de teclado para el campo de texto
        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == '\n') evaluateExpression();
                else if (c == '\b') backspace();
                else {
                    String s = String.valueOf(c);
                    if (s.equals(".")) appendToExpression(".");
                    else if (TOOLTIPS.containsKey(s) || s.matches("[0-9]")) appendToExpression(s);
                }
            }
        });
        display.setFocusable(true);
        display.requestFocusInWindow();
    }

    // Maneja las acciones de los botones
    private void handleButton(String cmd) {
        switch (cmd) {
            case "←": backspace(); break;
            case "C": clearAll(); break;
            case "=": evaluateExpression(); break;
            default:
                // Para funciones, agregar el nombre seguido de paréntesis
                if (FUNCTIONS.contains(cmd)) appendToExpression(cmd + "(");
                else appendToExpression(cmd);
        }
    }

    // Agrega caracteres a la expresión actual con validación
    private void appendToExpression(String s) {
        // Validación para puntos decimales múltiples
        if (s.equals(".")) {
            String expr = expression.toString();
            int i = expr.length() - 1;
            while (i >= 0 && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) i--;
            String token = expr.substring(i + 1);
            if (token.contains(".")) return;
            if (token.isEmpty()) s = "0.";
        }
        expression.append(s);
        display.setText(expression.toString());
    }

    // Elimina el último carácter de la expresión
    private void backspace() {
        if (expression.length() > 0) {
            expression.setLength(expression.length() - 1);
            display.setText(expression.toString());
        }
    }

    // Limpia toda la expresión
    private void clearAll() {
        expression.setLength(0);
        display.setText("");
    }

    // Evalúa la expresión matemática
    private void evaluateExpression() {
        String expr = expression.toString();
        if (expr.isEmpty()) return;

        // Validación de paréntesis balanceados
        if (!areParenthesesBalanced(expr)) {
            showError("Los paréntesis no están balanceados");
            return;
        }

        try {
            double result = evaluate(expr);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                showError("Resultado indefinido o infinito");
            } else {
                String res = String.valueOf(result);
                display.setText(res);
                expression.setLength(0);
                expression.append(res);
            }
        } catch (ArithmeticException ae) {
            showError("Error aritmético: " + ae.getMessage());
        } catch (IllegalArgumentException iae) {
            showError("Error de argumento: " + iae.getMessage());
        } catch (EmptyStackException ese) {
            showError("Expresión incompleta o mal formada");
        } catch (Exception e) {
            showError("Error desconocido: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // Verifica si los paréntesis están balanceados
    private boolean areParenthesesBalanced(String expr) {
        int balance = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }

    // Algoritmo shunting-yard para evaluar expresiones
    private double evaluate(String expr) {
        List<String> tokens = tokenize(expr);
        List<String> output = new ArrayList<>();
        Stack<String> ops = new Stack<>();

        for (String token : tokens) {
            if (token.matches("\\d+(?:\\.\\d+)?")) {  // Números
                output.add(token);
            } else if (FUNCTIONS.contains(token)) {  // Funciones
                ops.push(token);
            } else if (PRECEDENCE.containsKey(token)) {  // Operadores
                while (!ops.isEmpty() && PRECEDENCE.containsKey(ops.peek()) &&
                       ((isLeftAssoc(token) && PRECEDENCE.get(token) <= PRECEDENCE.get(ops.peek())) ||
                        (!isLeftAssoc(token) && PRECEDENCE.get(token) < PRECEDENCE.get(ops.peek())))) {
                    output.add(ops.pop());
                }
                ops.push(token);
            } else if (token.equals("(")) {  // Paréntesis izquierdo
                ops.push(token);
            } else if (token.equals(")")) {  // Paréntesis derecho
                while (!ops.isEmpty() && !ops.peek().equals("(")) output.add(ops.pop());
                ops.pop();
                if (!ops.isEmpty() && FUNCTIONS.contains(ops.peek())) output.add(ops.pop());
            }
        }
        while (!ops.isEmpty()) output.add(ops.pop());

        // Evaluación de la notación polaca inversa
        Stack<Double> st = new Stack<>();
        for (String tok : output) {
            if (tok.matches("\\d+(?:\\.\\d+)?")) {
                st.push(Double.parseDouble(tok));
            } else if (FUNCTIONS.contains(tok)) {
                st.push(applyFunction(tok, st.pop()));
            } else {
                double b = st.pop();
                double a = st.pop();
                st.push(applyOperator(tok, a, b));
            }
        }
        return st.pop();
    }

    // Divide la expresión en tokens (números, operadores, funciones)
    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {  // Números y decimales
                int j = i;
                while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                tokens.add(expr.substring(i, j));
                i = j;
            } else if (Character.isLetter(c)) {  // Funciones
                int j = i;
                while (j < expr.length() && Character.isLetter(expr.charAt(j))) j++;
                tokens.add(expr.substring(i, j));
                i = j;
            } else {  // Operadores y paréntesis
                tokens.add(String.valueOf(c));
                i++;
            }
        }
        return tokens;
    }

    // Determina si un operador es asociativo por la izquierda
    private static boolean isLeftAssoc(String op) {
        return !op.equals("^");  // La potenciación es asociativa por la derecha
    }

    // Aplica funciones trigonométricas y matemáticas
    private double applyFunction(String func, double v) {
        switch (func) {
            case "sin": return Math.sin(Math.toRadians(v));
            case "cos": return Math.cos(Math.toRadians(v));
            case "tan": return Math.tan(Math.toRadians(v));
            case "asin": return Math.toDegrees(Math.asin(v));
            case "acos": return Math.toDegrees(Math.acos(v));
            case "atan": return Math.toDegrees(Math.atan(v));
            case "sqrt": return Math.sqrt(v);
            default: throw new IllegalArgumentException("Función desconocida");
        }
    }

    // Aplica operaciones matemáticas básicas
    private double applyOperator(String op, double a, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("División por cero");
                return a / b;
            case "%": return a * b / 100;
            case "^": return Math.pow(a, b);
            default: throw new IllegalArgumentException("Operador desconocido");
        }
    }

    // Muestra mensajes de error
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        clearAll();
    }

    // Método principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScientificCalculator calc = new ScientificCalculator();
            calc.setVisible(true);
        });
    }
}