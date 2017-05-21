import org.junit.Test;

import java.io.StringReader;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Dylan on 8/05/17.
 */
public class TestNodes {

    /*
     ************************* Factories *************************
     */

    @Test
    public void programFactory_create_noCircularDependency() {
        testNodeFactoryStackOverflow(new StatementNode.NodeFactory());
    }

    @Test
    public void actionFactory_create_noCircularDependency() {
        testNodeFactoryStackOverflow(new ActionNode.NodeFactory());
    }

    @Test
    public void expressionFactory_create_noCircularDependency() {
        testNodeFactoryStackOverflow(new ExpressionNode.NodeFactory());
    }

    @Test
    public void numberFactory_create_noCircularDependency() {
        testNodeFactoryStackOverflow(new NumberNode.NodeFactory());
    }

    @Test
    public void operationFactory_create_noCircularDependency() {
        testNodeFactoryStackOverflow(new BooleanOperationNode.NodeFactory());
    }

    private void testNodeFactoryStackOverflow(ParsableNode.Factory<?> factory) {
        Scanner scanner = new Scanner(new StringReader("test"));
        factory.canStartWith(scanner);
    }

    /*
     ************************* Number *************************
     */

    @Test
    public void parseNumber_withPositiveInteger_executesToSameNumber() {
        NodeTesters.NUMBER.testParseNode("3", "3");
    }

    @Test
    public void parseNumber_withNegativeInteger_executesToSameNumber() {
        NodeTesters.NUMBER.testParseNode("-9", "-9");
    }

    @Test
    public void parseNumber_withSpaces_ignoresSpaces() {
        NodeTesters.NUMBER.testParseNode("  4  ", "4");
    }

    @Test
    public void parseNumber_withMultipleDigits_getsInteger() {
        NodeTesters.NUMBER.testParseNode("-987", "-987");
    }

    @Test
    public void parseNumber_withNegativeInteger_getsInteger() {
        NodeTesters.NUMBER.testParseNode("-9", "-9");
    }

    @Test
    public void parseNumber_withSpaceAfterMinus_fails() {
        NodeTesters.NUMBER.testParseNodeFails("- 9", ParserFailureType.NUMBER_FORMAT);
    }

    /*
     ************************* Operations *************************
     */

    @Test
    public void parseAdd_withAdd1And2NoSpaces_parsesCorrectly() {
        NodeTesters.ADD.testParseNode("add(1,2)", "add(1,2)");
    }

    @Test
    public void parseAdd_oneParam_fails() {
        NodeTesters.ADD.testParseNodeFails(
                "add(1)",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void parseAdd_3Params_parses() {
        NodeTesters.ADD.testParseNodeFails(
                "add(1,2,3)",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void parseAdd_withAdd1And2WithSpaces_parsesCorrectly() {
        NodeTesters.ADD.testParseNode("  add  (  1  ,  2  )  ", "add(1,2)");
    }

    @Test
    public void parseAdd_multipleDigitAndNegativeNums_parsesCorrectly() {
        NodeTesters.ADD.testParseNode("  add  (  10  ,  -2  )  ", "add(10,-2)");
    }

    @Test
    public void parseAdd_missingBracket_error() {
        NodeTesters.ADD.testParseNodeFails(
                "add 10,-2)", ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void executeAdd_twoPositiveArgs_sumsCorrectly() {
        NodeTesters.ADD.testExecute("add(10,99)", 109);
    }

    @Test
    public void executeAdd_twoNegativeArgs_sumsCorrectly() {
        NodeTesters.ADD.testExecute("add(-5,-123)", -128);
    }

    @Test
    public void executeOperationAdd_twoNegativeArgs_sumsCorrectly() {
        NodeTesters.OPERATION.testExecute("add(-5,-123)", -128);
    }

    @Test
    public void parseOperationSubtract_withAdd1And2NoSpaces_parsesCorrectly() {
        NodeTesters.OPERATION.testParseNode("sub(1,2)", "sub(1,2)");
    }

    @Test
    public void executeOperationSubtract_twoPositiveArgs_sumsCorrectly() {
        NodeTesters.OPERATION.testExecute("sub(10,99)", -89);
    }

    /*
     ************************* DecorableOptionalParamNode *************************
     */

    @Test
    public void parseAction_turnL_actionIsRecognised() {
        // Ensure test is sane
        assertEquals(false, ActionNode.ALL_ACTIONS.get("turnL").hasOptionalParam);

        NodeTesters.ACTION.testParseNode("turnL;", "turnL;");
    }

    @Test
    public void parseAction_turnR_actionIsRecognised() {
        NodeTesters.ACTION.testParseNode("turnR;", "turnR;");
    }

    @Test
    public void parseAction_unknownAction_error() {
        NodeTesters.ACTION.testParseNodeFails(
                "someUnknownAction;",
                ParserFailureType.NON_ONE_MATCHES
        );
    }

    @Test
    public void parseAction_noSemicolon_error() {
        NodeTesters.ACTION.testParseNodeFails(
                "turnL",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void parseAction_oneParamForOneParamAction_success() {
        // Ensure test is sane
        assertEquals(true, ActionNode.ALL_ACTIONS.get("wait").hasOptionalParam);

        NodeTesters.ACTION.testParseNode("wait(3);", "wait(3);");
    }

    @Test
    public void parseAction_noParamsForOneParamAction_success() {
        NodeTesters.ACTION.testParseNode("wait;", "wait;");
    }

    @Test
    public void parseSensor_fuelLeft_sensorIsRecognised() {
        // Ensure test is sane
        assertEquals(false, SensorNode.ALL_SENSORS.get("fuelLeft").hasOptionalParam);

        NodeTesters.SENSOR.testParseNode("fuelLeft", "fuelLeft");
    }

    @Test
    public void parseSensor_sensorRequiringNoArgs_error() {
        // Ensure test is sane
        assertEquals(false, SensorNode.ALL_SENSORS.get("fuelLeft").hasOptionalParam);

        NodeTesters.PROGRAM.testParseNodeFails(
                "if(eq(fuelLeft(3), 2)){turnL;}",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void parseSensor_unrecognisedSensor_parseError() {
        NodeTesters.SENSOR.testParseNodeFails(
                "unrecognisedSensor",
                ParserFailureType.NON_ONE_MATCHES
        );
    }

    @Test
    public void parseSensor_noArgsForSensorThatAllowsOneArg_parses() {
        // Ensure test is sane
        assertEquals(true, SensorNode.ALL_SENSORS.get("barrelLR").hasOptionalParam);

        NodeTesters.SENSOR.testParseNode("barrelLR", "barrelLR");
    }

    @Test
    public void parseSensor_oneArgForSensorThatAllowsOneArg_parses() {
        // Ensure test is sane
        assertEquals(true, SensorNode.ALL_SENSORS.get("barrelLR").hasOptionalParam);

        NodeTesters.SENSOR.testParseNode("barrelLR(5)", "barrelLR(5)");
    }

    /*
     ************************* ProgramNode *************************
     */

    @Test
    public void parseProgram_programWith1Action_parsesCorrectly() {
        NodeTesters.PROGRAM.testParseNode("turnAround;", "turnAround;");
    }

    @Test
    public void parseProgram_programWith2Actions_parsesCorrectly() {
        NodeTesters.PROGRAM.testParseNode("turnL;turnR;", "turnL;turnR;");
    }

    @Test
    public void parseProgram_missingSemicolon_parsesCorrectly() {
        NodeTesters.PROGRAM.testParseNodeFails(
                "turnL turnR;",
                ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    /*
     ************************* BlockNode *************************
     */

    @Test
    public void parseBlock_noStatements_error() {
        NodeTesters.BLOCK.testParseNodeFails(
                "{}", ParserFailureType.WRONG_NUMBER_OF_STATEMENTS
        );
    }

    @Test
    public void parseBlock_oneStatement_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNode("{turnL;}", "{turnL;}");
    }

    @Test
    public void parseBlock_twoStatements_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNode("{turnL;turnR;}", "{turnL;turnR;}");
    }

    @Test
    public void parseBlock_missingSemicolon_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNodeFails(
                "{turnL}", ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    @Test
    public void parseBlock_withSpaces_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNode(" { turnL ; } ", "{turnL;}");
    }

    @Test
    public void parseBlock_missingBrace_parsesCorrectly() {
        NodeTesters.BLOCK.testParseNodeFails(
                "{turnL;", ParserFailureType.WRONG_MIDDLE_OR_END_OF_NODE
        );
    }

    /*
     ************************* LoopNode *************************
     */

    @Test
    public void parseLoop_oneStatement_parsesCorrectly() {
        NodeTesters.LOOP.testParseNode("loop{turnL;}", "loop{turnL;}");
    }

    @Test
    public void parseLoop_missingBracket_error() {
        NodeTesters.LOOP.testParseNodeFails(
                "loop turnL;}",
                ParserFailureType.WRONG_NODE_START // error in start of block
        );
    }

    @Test
    public void parseLoop_loopInsideLoop_parses() {
        NodeTesters.LOOP.testParseNode("loop{loop{turnL;}}", "loop{loop{turnL;}}");
    }

    /*
     ************************* ConditionNode *************************
     */

    @Test
    public void parseLessThan_1Then2_parses() {
        NodeTesters.CONDITION.testParseNode("lt(1,2)", "lt(1,2)");
    }

    @Test
    public void executeLessThan_1Then2_parses() {
        NodeTesters.CONDITION.testExecute("lt(1,2)", true);
    }

    /*
     ************************* BooleanOperationNode *************************
     */

    @Test
    public void parseOr_simplestCondition_parses() {
        NodeTesters.BOOLEAN.testParseNode("or(eq(1,1),lt(1,2))", "or(eq(1,1),lt(1,2))");
    }

    @Test
    public void executeOr_bothConditionsTrue_returnsTrue() {
        NodeTesters.BOOLEAN.testExecute("or(eq(1,1),lt(1,2))", true);
    }

    @Test
    public void executeOr_oneConditionsTrue_returnsTrue() {
        NodeTesters.BOOLEAN.testExecute("or(eq(1,2),lt(1,2))", true);
    }

    @Test
    public void executeOr_noConditionsTrue_returnsFalse() {
        NodeTesters.BOOLEAN.testExecute("or(eq(1,2),gt(1,2))", false);
    }

    @Test
    public void executeNot_inputTrue_returnsFalse() {
        NodeTesters.BOOLEAN.testExecute("not(eq(1,1))", false);
    }

    /*
     ************************* ConditionBlockNode (if/while) *************************
     */

    @Test
    public void parseIf_withLessThan_parses() {
        NodeTesters.IF.testParseNode("if(lt(1,2)){turnL;}", "if(lt(1,2)){turnL;}");
    }

    @Test
    public void parseIf_withElse_parses() {
        NodeTesters.IF.testParseNode(
                "if(gt(1,2)){turnL;}else{turnR;}",
                "if(gt(1,2)){turnL;}else{turnR;}"
        );
    }

    @Test
    public void parseIf_withElif_parses() {
        NodeTesters.IF.testParseNode(
                "if(gt(1,2)){turnL;}elif(gt(1,2)){turnR;}",
                "if(gt(1,2)){turnL;}elif(gt(1,2)){turnR;}"
        );
    }

    @Test
    public void parseIf_withElifAndElse_parses() {
        NodeTesters.IF.testParseNode(
                "if(gt(1,2)){turnL;}elif(gt(1,2)){turnR;}else{turnAround;}",
                "if(gt(1,2)){turnL;}elif(gt(1,2)){turnR;}else{turnAround;}"
        );
    }

    @Test
    public void parseIf_with2ElifAndElse_parses() {
        NodeTesters.IF.testParseNode(
                "if(gt(1,2)){turnL;}" +
                        "elif(gt(1,2)){turnR;}" +
                        "elif(gt(3,4)){turnR;}" +
                        "else{turnAround;}",
                "if(gt(1,2)){turnL;}" +
                        "elif(gt(1,2)){turnR;}" +
                        "elif(gt(3,4)){turnR;}" +
                        "else{turnAround;}"
        );
    }

    @Test
    public void parseWhile_withLessThan_parses() {
        NodeTesters.WHILE.testParseNode("while(lt(1,2)){turnL;}", "while(lt(1,2)){turnL;}");
    }

    /*
     ************************* Variable/Assignment Nodes *************************
     */

    @Test
    public void parseDeclaration_validName_parses() {
        NodeTesters.PROGRAM.testParseNode("vars $a1B;", "vars $a1B;");
    }

    @Test
    public void parseDeclaration_multipleValidNames_parses() {
        NodeTesters.PROGRAM.testParseNode("vars $a,$b;", "vars $a,$b;");
    }

    @Test
    public void parseVariable_numberAfterDollar_error() {
        NodeTesters.PROGRAM.testParseNodeFails(
                "vars $1B;", ParserFailureType.VARIABLE_FORMAT
        );
    }

    @Test
    public void parseAssignment_assignToDefinedVariable_parses() {
        NodeTesters.PROGRAM.testParseNode("vars $abc;$abc=2;", "vars $abc;$abc=2;");
    }

    @Test
    public void executeAssignment_assignToDefinedVariable_scopeHasValue2() {
        VariableScopeNode.Scope scope = testExecuteAssignment("vars $abc;$abc=2;");
        assertEquals(2, (int) scope.getValue("$abc"));
    }

    @Test
    public void parseAssignment_assignToUndefinedVariable_error() {
        NodeTesters.PROGRAM.testParseNodeFails(
                "vars $a;$b=2;",
                ParserFailureType.UNDEFINED_VARIBLE_ASSIGNMENT
        );
    }

    @Test
    public void executeAssignment_readUndefinedVariable_error() {
        NodeTesters.PROGRAM.testParseNodeFails(
                "if(eq($abc,2)){turnL;}",
                ParserFailureType.UNDEFINED_VARIBLE_ACCESS
        );
    }

    @Test
    public void executeAssignment_declareInsideBlock_notAccessibleOutsideBlock() {
        NodeTesters.PROGRAM.testParseNodeFails(
                "if(eq(1,1)){vars $a;$a=2;}vars $b;$b=$a;", // $b accessed outside
                ParserFailureType.UNDEFINED_VARIBLE_ACCESS
        );
    }

    @Test
    public void executeAssignment_reassignInsideBlock_newValueAccessibleOutside() {
        VariableScopeNode.Scope scope = testExecuteAssignment(
                "vars $a;$a=1;if(eq(1,1)){$a=2;}vars $b;$b=$a;"
        );
        assertEquals(2, (int) scope.getValue("$b"));
    }

    @Test
    public void executeAssignment_redefineAndAssignInsideBlock_doesntOverrideOuterValue() {
        VariableScopeNode.Scope scope = testExecuteAssignment(
                "vars $a;$a=1;if(eq(1,1)){vars $a;$a=2;}vars $b;$b=$a;"
        );
        assertEquals((int) scope.getValue("$b"), 1);
    }

    private VariableScopeNode.Scope testExecuteAssignment(String input) {
        ProgramNode scopeNode = new ProgramNode(null);
        scopeNode.parse(newScanner(input), new Logger.SystemOutputLogger());
        scopeNode.execute(null);
        return scopeNode.getExecutionScope();
    }

    /*
     ************************* Utils *************************
     */

    private static Scanner newScanner(String program) {
        Scanner scanner = new Scanner(new StringReader(program));
        scanner.useDelimiter(AbstractParsableNode.DEFAULT_DELIMITER);
        return scanner;
    }

    private enum NodeTesters {
        // For factories that always instantiate one type of node
        NUMBER(NumberNode.NodeFactory::new),
        ACTION(ActionNode.NodeFactory::new),
        SENSOR(SensorNode.NodeFactory::new),
        LOOP(LoopNode.NodeFactory::new),
        IF(IfNode.NodeFactory::new),
        WHILE(WhileNode.NodeFactory::new),
        PROGRAM(factoryFromFactoryFunction(ProgramNode::new)),
        BLOCK(factoryFromFactoryFunction(BlockNode::new)),
        ADD(factoryFromFactoryFunction(OperationNode.Operations.ADD::create)),

        // For factories that have to pick a node type (e.g. DelegatorFactory)
        BOOLEAN(BooleanOperationNode.NodeFactory::new),
        CONDITION(ConditionNode.NodeFactory::new),
        OPERATION(OperationNode.NodeFactory::new);

        private final Supplier<? extends ParsableNode.Factory<? extends ParsableNode<?>>>
                factorySupplier;

        NodeTesters(Supplier<? extends ParsableNode.Factory<? extends ParsableNode<?>>>
                            factorySupplier) {
            this.factorySupplier = factorySupplier;
        }

        private static Supplier<? extends ParsableNode.Factory<? extends ParsableNode<?>>>
        factoryFromFactoryFunction(
                Function<ParsableNode<?>, ? extends ParsableNode<?>> nodeSupplier) {

            return () ->
                    new ParsableNode.Factory<ParsableNode<?>>() {
                        @Override
                        public ParsableNode<?> create(Scanner scannerNotToBeModified,
                                                      ParsableNode<?> parentNode) {
                            return nodeSupplier.apply(parentNode);
                        }

                        @Override
                        public boolean canStartWith(Scanner scannerNotToBeModified) {
                            return true;
                        }
                    };
        }

        public void testParseNode(String program, String expectedCode) {
            ParsableNode<?> node = newNodeWithInput(program);
            assertEquals(expectedCode, node.toString());
        }

        public void testParseNodeFails(String incorrectProgram,
                                       ParserFailureType errorType) {
            try {
                newNodeWithInput(incorrectProgram);
                fail();
            } catch (ParserFailureException exception) {
                assertEquals(errorType, exception.getType());
            }
        }

        public void testExecute(String program, Object expected) {
            testExecute(program, expected, null);
        }

        public void testExecute(String program, Object expected, Robot robot) {
            ParsableNode<?> node = newNodeWithInput(program);
            assertEquals(expected, node.execute(robot));
        }

        private ParsableNode<?> newNodeWithInput(String program) {
            Scanner scanner = newScanner(program);
            ParsableNode<?> node = factorySupplier.get().create(scanner, null);
            node.parse(scanner, new Logger.SystemOutputLogger());
            return node;
        }
    }
}
