Feature: Solving Towers of Hanoi puzzle

  Scenario: Solver should not try to solve a game if it is not in the starting \
  state
    Given a new game with 3 disks
    And no solve error should be displayed to the user
    And the user enters the command "1 2"
    Then the user enters the command "solve"
    And the game should not be in the solved state
    And a solve error should be displayed to the user

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