import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private String expression = "";

    public Calculator() {
        setTitle("Calculator");
        setSize(350, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 40)); // result font
        display.setHorizontalAlignment(JTextField.LEFT); // align text
        display.addKeyListener(new KeyAdapter() { // to support for keyboard also
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (Character.isDigit(keyChar) || keyChar == '+' || keyChar == '-' || keyChar == '*' || keyChar == '/' || keyChar == '.') {
                    appendToExpression(String.valueOf(keyChar));
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateResult();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteLastCharacter();
                }
            }
        });
        add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4));
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "Del", "0", ".", "+",
                "C", "="
        };
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            button.setFont(new Font("Arial", Font.PLAIN, 30)); // buttons font
            buttonPanel.add(button);
            // Adjust button color based on type
            if (label.matches("[0-9.]")) {
                button.setBackground(new Color(200, 200, 200)); // light gray background color for numbers and dot
            } else if (label.equals("Del")) {
                button.setBackground(new Color(200, 200, 200)); // and del
            } else if (label.equals("C")) {
                button.setBackground(Color.BLUE); // blue background for "C"
                button.setForeground(Color.BLACK); // black text color
            }
            else if (label.equals("=")) {
                button.setBackground(Color.BLUE); // blue background for "="
                button.setForeground(Color.WHITE); // white text color
            }
            else {
                button.setBackground(new Color(150, 150, 150)); // dark gray background coperands
            }
        }
        add(buttonPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "=":
                calculateResult();
                break;
            case "C":
                clearDisplay();
                break;
            case "Del":
                deleteLastCharacter();
                break;
            default:
                appendToExpression(command);
        }
    }

    private void appendToExpression(String value) {
        expression += value;
        updateDisplay();
    }

    private void clearDisplay() { // when pressing on "C" button
        expression = "";
        updateDisplay();
    }

    private void deleteLastCharacter() { // when pressing on "Del" button
        if (!expression.isEmpty()) {
            expression = expression.substring(0, expression.length() - 1);
            updateDisplay();
        }
    }

    private void calculateResult() { // when pressing on "=" button
        try {
            double result = evaluateExpression(expression);
            display.setText(formatResult(result));
            expression = formatResult(result); // keeps formatted result for further operations in "expression"
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    private String formatResult(double result) {
        String formattedResult = String.format("%.5f", result); // format result to at most 5 decimals after the point and remove trailing zeros
        formattedResult = formattedResult.replaceAll("\\.0*$", "").replaceAll("(\\.[1-9]*[1-9])0*$", "$1"); // remove trailing zeros and decimal point if no digits after it
        return formattedResult;
    }

    private double evaluateExpression(String expression) { // the calculating algorithm
        String[] tokens = expression.split("(?=[-+])|(?<=[-+])");
        double result = 0;
        char operator = '+';
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-")) {
                operator = token.charAt(0);
            } else {
                String[] subTokens = token.split("(?=[*/])|(?<=[*/])");
                double subResult = Double.parseDouble(subTokens[0]);
                for (int i = 1; i < subTokens.length; i += 2) {
                    char subOperator = subTokens[i].charAt(0);
                    double operand = Double.parseDouble(subTokens[i + 1]);
                    switch (subOperator) {
                        case '*':
                            subResult *= operand;
                            break;
                        case '/':
                            if (operand == 0) {
                                throw new ArithmeticException("Division by zero");
                            }
                            subResult /= operand;
                            break;
                    }
                }
                switch (operator) {
                    case '+':
                        result += subResult;
                        break;
                    case '-':
                        result -= subResult;
                        break;
                }
            }
        }
        return result;
    }

    private void updateDisplay() { // updates the text displayed by being executed after appendToExpression/Clear/Delete
        display.setText(expression);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Calculator calculator = new Calculator();
                calculator.setVisible(true);
            }
        });
    }
}
