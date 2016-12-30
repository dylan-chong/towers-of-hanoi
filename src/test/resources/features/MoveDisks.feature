Feature: Move disks between stacks

  Background:
    Given a starting-game stack with 3 disks and 3 stacks

  Scenario: Game start
    Then stack 1 should have 3 disks
    And all stacks except stack 1 should have 0 disks

  Scenario: Move disk from full stack to empty stack
    When the user moves a disk from stack 1 to stack 3
    Then stack 1 should have 2 disks
    And stack 2 should have 0 disks
    And stack 3 should have 1 disk

  Scenario: Should not be able to move disk from empty stack
    When the user tries to move a disk from stack 2 to stack 3 (expect fail)
    Then stack 1 should have 3 disks
    And stack 2 should have 0 disks
    And stack 3 should have 0 disk

