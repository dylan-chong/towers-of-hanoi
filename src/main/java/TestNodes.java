import org.junit.Test;

import java.io.StringReader;
import java.util.Scanner;
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
        testNodeFactoryStackOverflow(new OperationNode.NodeFactory());
    }

    private void testNodeFactoryStackOverflow(ParsableNode.Factory<?> factory) {
        Scanner scanner = new Scanner(new StringReader("test"));
        factory.canStartWith(scanner);
    }

    /*
     ************************* Number *************************
     */

    @Test
    public void parseNumber_withPositiveInteger_evaluatesToSameNumber() {
        NodeTesters.NUMBER.testParseNode("3", "3");
    }

    @Test
    public void parseNumber_withNegativeInteger_evaluatesToSameNumber() {
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
    public void evaluateAdd_twoPositiveArgs_sumsCorrectly() {
        NodeTesters.ADD.testEvaluate("add(10,99)", 109);
    }

    @Test
    public void evaluateAdd_twoNegativeArgs_sumsCorrectly() {
        NodeTesters.ADD.testEvaluate("add(-5,-123)", -128);
    }

    @Test
    public void evaluateOperationAdd_twoNegativeArgs_sumsCorrectly() {
        NodeTesters.OPERATION.testEvaluate("add(-5,-123)", -128);
    }

    @Test
    public void parseOperationSubtract_withAdd1And2NoSpaces_parsesCorrectly() {
        NodeTesters.OPERATION.testParseNode("sub(1,2)", "sub(1,2)");
    }

    @Test
    public void evaluateOperationSubtract_twoPositiveArgs_sumsCorrectly() {
        NodeTesters.OPERATION.testEvaluate("sub(10,99)", -89);
    }

    /*
     ************************* FunctionWrapper *************************
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
    public void evaluateLessThan_1Then2_parses() {
        NodeTesters.CONDITION.testEvaluate("lt(1,2)", true);
    }

    /*
     ************************* ConditionBlockNode (if/while) *************************
     */

    @Test
    public void parseIf_withLessThan_parses() {
        NodeTesters.IF.testParseNode("if(lt(1,2)){turnL;}", "if(lt(1,2)){turnL;}");
    }

    @Test
    public void parseWhile_withLessThan_parses() {
        NodeTesters.WHILE.testParseNode("while(lt(1,2)){turnL;}", "while(lt(1,2)){turnL;}");
    }


    /*
     ************************* Utils *************************
     */

    private static Scanner newScanner(String program) {
        Scanner scanner = new Scanner(new StringReader(program));
        scanner.useDelimiter(ParsableNode.DEFAULT_DELIMITER);
        return scanner;
    }

    private enum NodeTesters {
        // For factories that always instantiate one type of node
        NUMBER(NumberNode.NodeFactory::new),
        ACTION(ActionNode.NodeFactory::new),
        LOOP(LoopNode.NodeFactory::new),
        IF(IfNode.NodeFactory::new),
        WHILE(WhileNode.NodeFactory::new),
        PROGRAM(factoryFromSupplier(ProgramNode::new)),
        BLOCK(factoryFromSupplier(BlockNode::new)),
        ADD(factoryFromSupplier(OperationNode.Operations.ADD::create)),

        // For DelegatorFactories / factories that have to pick a node type
        CONDITION(ConditionNode.NodeFactory::new),
        OPERATION(OperationNode.NodeFactory::new);

        private final Supplier<? extends ParsableNode.Factory<? extends ParsableNode<?>>>
                factorySupplier;

        NodeTesters(Supplier<? extends ParsableNode.Factory<? extends ParsableNode<?>>>
                            factorySupplier) {
            this.factorySupplier = factorySupplier;
        }

        private static Supplier<? extends ParsableNode.Factory<? extends ParsableNode<?>>>
        factoryFromSupplier(Supplier<? extends ParsableNode<?>> nodeSupplier) {
            return () ->
                    new ParsableNode.Factory<ParsableNode<?>>() {
                        @Override
                        public ParsableNode<?> create(Scanner scannerNotToBeModified) {
                            return nodeSupplier.get();
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

        public void testEvaluate(String program, Object expected) {
            testEvaluate(program, expected, null);
        }

        public void testEvaluate(String program, Object expected, Robot robot) {
            Scanner scanner = newScanner(program);
            ParsableNode<?> node = factorySupplier.get().create(scanner);
            node.parse(scanner, new Logger.SystemOutputLogger());
            assertEquals(expected, node.evaluate(robot));
        }

        private ParsableNode<?> newNodeWithInput(String program) {
            Scanner scanner = newScanner(program);
            ParsableNode<?> node = factorySupplier.get().create(scanner);
            node.parse(scanner, new Logger.SystemOutputLogger());
            return node;
        }
    }
}
