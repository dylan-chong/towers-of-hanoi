Feature: Solving Towers of Hanoi puzzle

  Scenario: Solver should not try to solve a game if it is not in the starting \
  state
    Given a new game
    And a new solver that uses the game
    When the solution from the solver is applied to the game
    Then the game should not be in the solved state
    And an error should be displayed to the user

#  Scenario Outline: GameSolver should be able to solve the puzzle with <numDisks> disks
#    Given a new game with <numDisks> disks
#    When the solver finds a solution
#    And the solution is applied to the game
#    Then the game should be solved
#
#    Examples:
#      | numDisks |
#      | 1        |
#      | 2        |