import java.util.NoSuchElementException;
import java.util.Objects;

public class Main {
    static Expression number = new IntConstantExpression(10); // A simple number or integer value

    static Expression addsTwoNumbers =
            new BinaryOperationExpression(Operator.PLUS, new IntConstantExpression(4), new IntConstantExpression(5));

    static Expression addsNumberToVariable =
            new BinaryOperationExpression(Operator.PLUS, new VariableExpression(new Name("x")),
                    new IntConstantExpression(4));

    static Expression doublingVariable =
            new BinaryOperationExpression(Operator.PLUS, new VariableExpression(new Name("x")),
                    new VariableExpression(new Name("x")));

    static Expression doublingVariableWithLet =
            new LetExpression(new Name("x"), new IntConstantExpression(10), doublingVariable);

    static Expression dontDivideByZero =
            new IfExpression(
                    new IntEqualsExpression(new IntConstantExpression(0), new VariableExpression(new Name("x"))),
                    new IntConstantExpression(0), new BinaryOperationExpression(Operator.DIVIDE,
                    new IntConstantExpression(10),
                    new VariableExpression(new Name("x"))));

    public static void main(String[] args) {
        /*// Evaluate a simple integer constant
        System.out.println(evaluate(number));

        // Evaluate an addition operation
        System.out.println(evaluate(addsTwoNumbers));

        // Add an integer value to a variable
        System.out.println(evaluate(addsNumberToVariable));*/

        // Create a global environment
        Environment globalEnv = Environment.add(new Name("x"), new IntValue(0), Environment.Empty);
        // Add a variable to itself (requires environment as programmer cannot specify the variable's value while using it
        System.out.println(evaluate(dontDivideByZero, globalEnv));
    }

    public static Value evaluate(Expression code, Environment env) {
        switch (code.getClass().getSimpleName()) {
            case "IntConstantExpression": { // Case when expression is a single integer value
                IntConstantExpression constant = (IntConstantExpression) code; // Get the expression and downcast to an int constant
                return new IntValue(constant.value);
            }

            case "BinaryOperationExpression": { // Case when expression is a binary operation
                BinaryOperationExpression operation = (BinaryOperationExpression) code; // Downcast the expression
                                                                                        // to a binary operation expression
                IntValue leftValue = (IntValue) (evaluate(operation.left, env)); // Left can either be a integer value or an
                                                                            // expression in itself. So evaluate it first.

                IntValue rightValue = (IntValue) (evaluate(operation.right, env));// Same as above comment for right

                switch(operation.operator) {
                    case PLUS:
                        return new IntValue(leftValue.value + rightValue.value);

                    case MINUS:
                        return new IntValue(leftValue.value - rightValue.value);

                    case TIMES:
                        return new IntValue(leftValue.value * rightValue.value);

                    case DIVIDE:
                        return new IntValue(leftValue.value / rightValue.value);

                    default:
                        throw new Error("Invalid operation");
                }
            }

            case "VariableExpression": {
                VariableExpression variable = (VariableExpression) code;
                return env.lookupValue(variable.name); // Find the binding of the required variable
            }

            case "LetExpression": {
                LetExpression let = (LetExpression) code;
                Value value = evaluate(let.value, env);
                return evaluate(let.body, Environment.add(let.variableName, value, env)); // Lexical Scoping -
                                                                                          // Look at the block immediately
                                                                                          // surrounding the variable usage
            }

            case "IntEqualsExpression": {
                IntEqualsExpression intEq = (IntEqualsExpression) code; // Downcast to an int equals expression
                IntValue leftValue = (IntValue) (evaluate(intEq.left, env)); // Find the left value
                IntValue rightValue = (IntValue) (evaluate(intEq.right, env)); // Find the right value

                if(leftValue.value == rightValue.value)
                    return (new BooleanValue(true));
                else return(new BooleanValue(false));
            }

            case "IfExpression": {
                IfExpression ifExp = (IfExpression) code; // Downcast to an if expression
                BooleanValue predicate = (BooleanValue) evaluate(ifExp.predicate, env); // Evaluate the predicate
                if(predicate.value)
                    return evaluate(ifExp.thenSide, env); // Execute the "then" part
                else return evaluate(ifExp.elseSide, env); // Execute the "else" part
            }

            default:
                throw new Error("Invalid value: " + code.getClass().getSimpleName());
        }
    }
}

abstract class Expression { // Expression - Number or Operation

}
 class IntConstantExpression extends Expression { // For an integer number  (2,10,11,etc.)
    int value;
    public IntConstantExpression(int v) {
        this.value = v;
    }
}

enum Operator { PLUS, MINUS, TIMES, DIVIDE }

class BinaryOperationExpression extends Expression { // For a binary operation (+, -, *, /, etc.)
    Operator operator;
    Expression left;
    Expression right;

    public BinaryOperationExpression(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
}

class VariableExpression extends Expression { // A variable like a or b
    Name name;

    public VariableExpression(Name name) {
        this.name = name;
    }
}

// Let expression
class LetExpression extends Expression {
    Name variableName;
    Expression value;
    Expression body;

    public LetExpression(Name variableName, Expression value, Expression body) {
        this.variableName = variableName;
        this.value = value;
        this.body = body;
    }
}

class IntEqualsExpression extends Expression {
    Expression left;
    Expression right;

    public IntEqualsExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
}

class IfExpression extends Expression {
    Expression predicate;
    Expression thenSide;
    Expression elseSide;

    public IfExpression(IntEqualsExpression predicate, Expression thenSide, Expression elseSide) {
        this.predicate = predicate;
        this.thenSide = thenSide;
        this.elseSide = elseSide;
    }
}

class Name {
    String name;

    public Name(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name1 = (Name) o;
        return Objects.equals(name, name1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
abstract class Value {

}
class IntValue extends Value { // For an integer value
    int value;
    IntValue(int v) {
        this.value = v;
    }

     @Override
     public String toString() {
         return "" + value;
     }
 }

class BooleanValue extends Value { // For a boolean value
    boolean value;
    BooleanValue(boolean v) {
        this.value = v;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}

 class Environment {
    static final Environment Empty = new Environment();

    Binding binding;
    Environment env;

    private Environment() { }

    static Environment add(Name name, Value value, Environment env) { // Add an environment to the current environment
        Binding binding = new Binding(name, value);
        Environment newEnv = new Environment();
        newEnv.binding = binding;
        newEnv.env = env;
        return newEnv;
    }

    Value lookupValue(Name name) { // Find the binding in the current environment
        if(this == Empty) // No bindings in the current environment
            throw new NoSuchElementException();
        if(this.binding.name.equals(name))
            return binding.value;
        return (this.env.lookupValue(name));
    }
}

class Binding {
    Name name;
    Value value;

    public Binding(Name name, Value value) {
        this.name = name;
        this.value = value;
    }
}