---
title: COMP307 Assignment 1
author:
    - Dylan Chong (Student ID - 300373593)
margin-left: 2.5cm
margin-right: 2.5cm
margin-top: 2.5cm
margin-bottom: 2.5cm
---

# Part 1

## Q1

See the end of each item for the result classKind and the actualClassKind. If
`isCorrect: true` then the result classKind equals the actualClassKind.

Test set accuracy: 90% correct (68:7)

0. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=3.0, petalLength=1.6, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
1. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=3.4, petalLength=1.6, petalWidth=0.4, classKind=SETOSA), actualClassKind: SETOSA 
2. isCorrect: true, result: IrisInstance(sepalLength=5.2, sepalWidth=3.5, petalLength=1.5, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
3. isCorrect: true, result: IrisInstance(sepalLength=5.2, sepalWidth=3.4, petalLength=1.4, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
4. isCorrect: true, result: IrisInstance(sepalLength=4.7, sepalWidth=3.2, petalLength=1.6, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
5. isCorrect: true, result: IrisInstance(sepalLength=4.8, sepalWidth=3.1, petalLength=1.6, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
6. isCorrect: true, result: IrisInstance(sepalLength=5.4, sepalWidth=3.4, petalLength=1.5, petalWidth=0.4, classKind=SETOSA), actualClassKind: SETOSA 
7. isCorrect: true, result: IrisInstance(sepalLength=5.2, sepalWidth=4.1, petalLength=1.5, petalWidth=0.1, classKind=SETOSA), actualClassKind: SETOSA 
8. isCorrect: true, result: IrisInstance(sepalLength=5.5, sepalWidth=4.2, petalLength=1.4, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
9. isCorrect: true, result: IrisInstance(sepalLength=4.9, sepalWidth=3.1, petalLength=1.5, petalWidth=0.1, classKind=SETOSA), actualClassKind: SETOSA 
10. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=3.2, petalLength=1.2, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
11. isCorrect: true, result: IrisInstance(sepalLength=5.5, sepalWidth=3.5, petalLength=1.3, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
12. isCorrect: true, result: IrisInstance(sepalLength=4.9, sepalWidth=3.1, petalLength=1.5, petalWidth=0.1, classKind=SETOSA), actualClassKind: SETOSA 
13. isCorrect: true, result: IrisInstance(sepalLength=4.4, sepalWidth=3.0, petalLength=1.3, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
14. isCorrect: true, result: IrisInstance(sepalLength=5.1, sepalWidth=3.4, petalLength=1.5, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
15. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=3.5, petalLength=1.3, petalWidth=0.3, classKind=SETOSA), actualClassKind: SETOSA 
16. isCorrect: true, result: IrisInstance(sepalLength=4.5, sepalWidth=2.3, petalLength=1.3, petalWidth=0.3, classKind=SETOSA), actualClassKind: SETOSA 
17. isCorrect: true, result: IrisInstance(sepalLength=4.4, sepalWidth=3.2, petalLength=1.3, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
18. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=3.5, petalLength=1.6, petalWidth=0.6, classKind=SETOSA), actualClassKind: SETOSA 
19. isCorrect: true, result: IrisInstance(sepalLength=5.1, sepalWidth=3.8, petalLength=1.9, petalWidth=0.4, classKind=SETOSA), actualClassKind: SETOSA 
20. isCorrect: true, result: IrisInstance(sepalLength=4.8, sepalWidth=3.0, petalLength=1.4, petalWidth=0.3, classKind=SETOSA), actualClassKind: SETOSA 
21. isCorrect: true, result: IrisInstance(sepalLength=5.1, sepalWidth=3.8, petalLength=1.6, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
22. isCorrect: true, result: IrisInstance(sepalLength=4.6, sepalWidth=3.2, petalLength=1.4, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
23. isCorrect: true, result: IrisInstance(sepalLength=5.3, sepalWidth=3.7, petalLength=1.5, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
24. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=3.3, petalLength=1.4, petalWidth=0.2, classKind=SETOSA), actualClassKind: SETOSA 
25. isCorrect: true, result: IrisInstance(sepalLength=6.6, sepalWidth=3.0, petalLength=4.4, petalWidth=1.4, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
26. isCorrect: true, result: IrisInstance(sepalLength=6.8, sepalWidth=2.8, petalLength=4.8, petalWidth=1.4, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
27. isCorrect: false, result: IrisInstance(sepalLength=6.7, sepalWidth=3.0, petalLength=5.0, petalWidth=1.7, classKind=VIRGINICA), actualClassKind: VIRGINICA 
28. isCorrect: true, result: IrisInstance(sepalLength=6.0, sepalWidth=2.9, petalLength=4.5, petalWidth=1.5, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
29. isCorrect: true, result: IrisInstance(sepalLength=5.7, sepalWidth=2.6, petalLength=3.5, petalWidth=1.0, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
30. isCorrect: true, result: IrisInstance(sepalLength=5.5, sepalWidth=2.4, petalLength=3.8, petalWidth=1.1, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
31. isCorrect: true, result: IrisInstance(sepalLength=5.5, sepalWidth=2.4, petalLength=3.7, petalWidth=1.0, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
32. isCorrect: true, result: IrisInstance(sepalLength=5.8, sepalWidth=2.7, petalLength=3.9, petalWidth=1.2, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
33. isCorrect: false, result: IrisInstance(sepalLength=6.0, sepalWidth=2.7, petalLength=5.1, petalWidth=1.6, classKind=VIRGINICA), actualClassKind: VIRGINICA 
34. isCorrect: true, result: IrisInstance(sepalLength=5.4, sepalWidth=3.0, petalLength=4.5, petalWidth=1.5, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
35. isCorrect: true, result: IrisInstance(sepalLength=6.0, sepalWidth=3.4, petalLength=4.5, petalWidth=1.6, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
36. isCorrect: true, result: IrisInstance(sepalLength=6.7, sepalWidth=3.1, petalLength=4.7, petalWidth=1.5, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
37. isCorrect: true, result: IrisInstance(sepalLength=6.3, sepalWidth=2.3, petalLength=4.4, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
38. isCorrect: true, result: IrisInstance(sepalLength=5.6, sepalWidth=3.0, petalLength=4.1, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
39. isCorrect: true, result: IrisInstance(sepalLength=5.5, sepalWidth=2.5, petalLength=4.0, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
40. isCorrect: true, result: IrisInstance(sepalLength=5.5, sepalWidth=2.6, petalLength=4.4, petalWidth=1.2, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
41. isCorrect: true, result: IrisInstance(sepalLength=6.1, sepalWidth=3.0, petalLength=4.6, petalWidth=1.4, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
42. isCorrect: true, result: IrisInstance(sepalLength=5.8, sepalWidth=2.6, petalLength=4.0, petalWidth=1.2, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
43. isCorrect: true, result: IrisInstance(sepalLength=5.0, sepalWidth=2.3, petalLength=3.3, petalWidth=1.0, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
44. isCorrect: true, result: IrisInstance(sepalLength=5.6, sepalWidth=2.7, petalLength=4.2, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
45. isCorrect: true, result: IrisInstance(sepalLength=5.7, sepalWidth=3.0, petalLength=4.2, petalWidth=1.2, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
46. isCorrect: true, result: IrisInstance(sepalLength=5.7, sepalWidth=2.9, petalLength=4.2, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
47. isCorrect: true, result: IrisInstance(sepalLength=6.2, sepalWidth=2.9, petalLength=4.3, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
48. isCorrect: true, result: IrisInstance(sepalLength=5.1, sepalWidth=2.5, petalLength=3.0, petalWidth=1.1, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
49. isCorrect: true, result: IrisInstance(sepalLength=5.7, sepalWidth=2.8, petalLength=4.1, petalWidth=1.3, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
50. isCorrect: true, result: IrisInstance(sepalLength=7.2, sepalWidth=3.2, petalLength=6.0, petalWidth=1.8, classKind=VIRGINICA), actualClassKind: VIRGINICA 
51. isCorrect: true, result: IrisInstance(sepalLength=6.2, sepalWidth=2.8, petalLength=4.8, petalWidth=1.8, classKind=VIRGINICA), actualClassKind: VIRGINICA 
52. isCorrect: false, result: IrisInstance(sepalLength=6.1, sepalWidth=3.0, petalLength=4.9, petalWidth=1.8, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
53. isCorrect: true, result: IrisInstance(sepalLength=6.4, sepalWidth=2.8, petalLength=5.6, petalWidth=2.1, classKind=VIRGINICA), actualClassKind: VIRGINICA 
54. isCorrect: true, result: IrisInstance(sepalLength=7.2, sepalWidth=3.0, petalLength=5.8, petalWidth=1.6, classKind=VIRGINICA), actualClassKind: VIRGINICA 
55. isCorrect: true, result: IrisInstance(sepalLength=7.4, sepalWidth=2.8, petalLength=6.1, petalWidth=1.9, classKind=VIRGINICA), actualClassKind: VIRGINICA 
56. isCorrect: true, result: IrisInstance(sepalLength=7.9, sepalWidth=3.8, petalLength=6.4, petalWidth=2.0, classKind=VIRGINICA), actualClassKind: VIRGINICA 
57. isCorrect: true, result: IrisInstance(sepalLength=6.4, sepalWidth=2.8, petalLength=5.6, petalWidth=2.2, classKind=VIRGINICA), actualClassKind: VIRGINICA 
58. isCorrect: false, result: IrisInstance(sepalLength=6.3, sepalWidth=2.8, petalLength=5.1, petalWidth=1.5, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
59. isCorrect: false, result: IrisInstance(sepalLength=6.1, sepalWidth=2.6, petalLength=5.6, petalWidth=1.4, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
60. isCorrect: true, result: IrisInstance(sepalLength=7.7, sepalWidth=3.0, petalLength=6.1, petalWidth=2.3, classKind=VIRGINICA), actualClassKind: VIRGINICA 
61. isCorrect: true, result: IrisInstance(sepalLength=6.3, sepalWidth=3.4, petalLength=5.6, petalWidth=2.4, classKind=VIRGINICA), actualClassKind: VIRGINICA 
62. isCorrect: true, result: IrisInstance(sepalLength=6.4, sepalWidth=3.1, petalLength=5.5, petalWidth=1.8, classKind=VIRGINICA), actualClassKind: VIRGINICA 
63. isCorrect: false, result: IrisInstance(sepalLength=6.0, sepalWidth=3.0, petalLength=4.8, petalWidth=1.8, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 
64. isCorrect: true, result: IrisInstance(sepalLength=6.9, sepalWidth=3.1, petalLength=5.4, petalWidth=2.1, classKind=VIRGINICA), actualClassKind: VIRGINICA 
65. isCorrect: true, result: IrisInstance(sepalLength=6.7, sepalWidth=3.1, petalLength=5.6, petalWidth=2.4, classKind=VIRGINICA), actualClassKind: VIRGINICA 
66. isCorrect: true, result: IrisInstance(sepalLength=6.9, sepalWidth=3.1, petalLength=5.1, petalWidth=2.3, classKind=VIRGINICA), actualClassKind: VIRGINICA 
67. isCorrect: true, result: IrisInstance(sepalLength=5.8, sepalWidth=2.7, petalLength=5.1, petalWidth=1.9, classKind=VIRGINICA), actualClassKind: VIRGINICA 
68. isCorrect: true, result: IrisInstance(sepalLength=6.8, sepalWidth=3.2, petalLength=5.9, petalWidth=2.3, classKind=VIRGINICA), actualClassKind: VIRGINICA 
69. isCorrect: true, result: IrisInstance(sepalLength=6.7, sepalWidth=3.3, petalLength=5.7, petalWidth=2.5, classKind=VIRGINICA), actualClassKind: VIRGINICA 
70. isCorrect: true, result: IrisInstance(sepalLength=6.7, sepalWidth=3.0, petalLength=5.2, petalWidth=2.3, classKind=VIRGINICA), actualClassKind: VIRGINICA 
71. isCorrect: true, result: IrisInstance(sepalLength=6.3, sepalWidth=2.5, petalLength=5.0, petalWidth=1.9, classKind=VIRGINICA), actualClassKind: VIRGINICA 
72. isCorrect: true, result: IrisInstance(sepalLength=6.5, sepalWidth=3.0, petalLength=5.2, petalWidth=2.0, classKind=VIRGINICA), actualClassKind: VIRGINICA 
73. isCorrect: true, result: IrisInstance(sepalLength=6.2, sepalWidth=3.4, petalLength=5.4, petalWidth=2.3, classKind=VIRGINICA), actualClassKind: VIRGINICA 
74. isCorrect: false, result: IrisInstance(sepalLength=5.9, sepalWidth=3.0, petalLength=5.1, petalWidth=1.8, classKind=VERSICOLOR), actualClassKind: VERSICOLOR 

## Q2

Accuracy:

- k=1, Results: 90% correct (68:7)
- k=3, Results: 96% correct (72:3)

## Q3

K-Nearest-Neighbour is a simple method that is able to find the class of an
instance well. It must be provided with a training set of data that does not
have too much noise. This method requires that all of the training data has the
same features and has a fixed set of classes. Otherwise, this method will not
work.

The K-Nearest-Neighbour method is prone to an incorrect point if k is too low.
If k is too high, then instances of the incorrect classes will be part of the
k-length set. Someone needs to find what a good k value is, so it is hard to
automate this method.

The method is also somewhat expensive to calculate the output class - `O (n log
k)`. Calculating the distance from the input to the each of the training
instances costs `O (n)`. Then, the cost of finding the closest `k` training
instances costs `O (n log k)`. 

## Q4

Steps:

1. Load the `iris.data` file to `l`, the set of instances
1. Divide `l` into `k=5` equal subgroups
1. For each subgroup set `s` in `l`:
    1. Use `t = l - s` as the training set
    1. Find the accuracy of calculating the classes of each instance in `s`
       against the training set `t`. This involves running the
       K-Nearest-Neighbour method on each item in `s`
1. Merge the accuracies to give the overall average accuracy

## Q5

Use the K-Means-Clustering method:

1. Make a set of 3 clusters `cs`, of type `{instances, centroid}`
1. For cluster `c` in `cs`:
    1. Set `c.centroid` to a random position in the data range
    1. Set `c.instances` to an empty set
1. For each instance `i` in the training set
    1. Find the closest centroid `c`
    1. Put `i` into `c.instances`
    1. Update `c.centroid` to the average centroid of the instances
1. Return the clusters `cs`

# Part 2

## Q1

These are the results for running part two:

    # Results for BaselineClassifier
    Classifier representation: mostCommonClassKind: live
    Results: 85% correct (23:4)
    live: 23 correct out of 23

    ## Results per instance:
    0. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    1. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    2. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    3. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    4. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    5. isCorrect: false, result: Instance(classKind=die, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=false, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=true}), actualClassKind: live 
    6. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    7. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    8. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=true, HISTOLOGY=false}), actualClassKind: live 
    9. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=false}), actualClassKind: live 
    10. isCorrect: false, result: Instance(classKind=die, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=false, VARICES=false, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    11. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=true}), actualClassKind: live 
    12. isCorrect: false, result: Instance(classKind=die, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=false, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    13. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    14. isCorrect: false, result: Instance(classKind=die, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=false, VARICES=false, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    15. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=false}), actualClassKind: live 
    16. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=true, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=true, HISTOLOGY=true}), actualClassKind: live 
    17. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=true, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    18. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=true, HISTOLOGY=true}), actualClassKind: live 
    19. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    20. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    21. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=true, VARICES=false, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    22. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=false, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    23. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=false, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    24. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=false, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    25. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    26. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=false, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 

    # Results for DecisionTree with ProperChildFactory (ProperChildFactory uses Gini Purity)
    Classifier representation: 
    FATIGUE = false: /69
        BIGLIVER = false: /12
            SGOT = false: /8
                Category live, prob = 100% : /8
            SGOT = true: /4
                ASCITES = true: /3
                    Category die, prob = 100% : /3
                ASCITES = false: /1
                    Category live, prob = 100% : /1
        BIGLIVER = true: /57
            VARICES = true: /46
                AGE = false: /36
                    ASCITES = true: /31
                        BILIRUBIN = false: /15
                            MALAISE = false: /8
                                HISTOLOGY = true: /1
                                    Category die, prob = 100% : /1
                                HISTOLOGY = false: /7
                                    Category live, prob = 100% : /7
                            MALAISE = true: /7
                                SPIDERS = true: /5
                                    Category live, prob = 100% : /5
                                SPIDERS = false: /2
                                    STEROID = false: /1
                                        Category live, prob = 100% : /1
                                    STEROID = true: /1
                                        Category die, prob = 100% : /1
                        BILIRUBIN = true: /16
                            SPIDERS = true: /13
                                ANTIVIRALS = true: /11
                                    Category live, prob = 100% : /11
                                ANTIVIRALS = false: /2
                                    FIRMLIVER = false: /1
                                        Category die, prob = 100% : /1
                                    FIRMLIVER = true: /1
                                        Category live, prob = 100% : /1
                            SPIDERS = false: /3
                                STEROID = false: /1
                                    Category die, prob = 100% : /1
                                STEROID = true: /2
                                    Category live, prob = 100% : /2
                    ASCITES = false: /5
                        STEROID = false: /3
                            ANOREXIA = false: /2
                                Category live, prob = 100% : /2
                            ANOREXIA = true: /1
                                Category die, prob = 100% : /1
                        STEROID = true: /2
                            Category die, prob = 100% : /2
                AGE = true: /10
                    ANOREXIA = false: /1
                        Category die, prob = 100% : /1
                    ANOREXIA = true: /9
                        SPLEENPALPABLE = true: /8
                            Category live, prob = 100% : /8
                        SPLEENPALPABLE = false: /1
                            Category die, prob = 100% : /1
            VARICES = false: /11
                FEMALE = false: /10
                    AGE = false: /7
                        Category die, prob = 100% : /7
                    AGE = true: /3
                        SPIDERS = true: /1
                            Category die, prob = 100% : /1
                        SPIDERS = false: /2
                            Category live, prob = 100% : /2
                FEMALE = true: /1
                    Category live, prob = 100% : /1
    FATIGUE = true: /30
        HISTOLOGY = true: /12
            AGE = false: /7
                STEROID = false: /2
                    FIRMLIVER = false: /1
                        Category die, prob = 100% : /1
                    FIRMLIVER = true: /1
                        Category live, prob = 100% : /1
                STEROID = true: /5
                    SPIDERS = true: /3
                        Category live, prob = 100% : /3
                    SPIDERS = false: /2
                        FIRMLIVER = false: /1
                            Category live, prob = 100% : /1
                        FIRMLIVER = true: /1
                            Category die, prob = 100% : /1
            AGE = true: /5
                Category live, prob = 100% : /5
        HISTOLOGY = false: /18
            Category live, prob = 100% : /18
    Results: 74% correct (20:7)
    live: 18 correct out of 23
    die: 2 correct out of 4

    ## Results per instance:
    0. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    1. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    2. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    3. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    4. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    5. isCorrect: false, result: Instance(classKind=die, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=false, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=true}), actualClassKind: live 
    6. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    7. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    8. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=true, HISTOLOGY=false}), actualClassKind: live 
    9. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=false}), actualClassKind: live 
    10. isCorrect: true, result: Instance(classKind=die, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=false, VARICES=false, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: die 
    11. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=true}), actualClassKind: live 
    12. isCorrect: false, result: Instance(classKind=die, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=false, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    13. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    14. isCorrect: true, result: Instance(classKind=die, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=false, VARICES=false, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: die 
    15. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=true, HISTOLOGY=false}), actualClassKind: live 
    16. isCorrect: false, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=true, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=true, HISTOLOGY=true}), actualClassKind: die 
    17. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=true, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=false, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    18. isCorrect: false, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=true, HISTOLOGY=true}), actualClassKind: die 
    19. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    20. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: live 
    21. isCorrect: false, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=false, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=false, ASCITES=true, VARICES=false, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: die 
    22. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=false, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    23. isCorrect: false, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=false, ASCITES=false, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=true}), actualClassKind: die 
    24. isCorrect: false, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=false, FATIGUE=false, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=false, SPLEENPALPABLE=false, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=false}), actualClassKind: die 
    25. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=false, FEMALE=false, STEROID=true, ANTIVIRALS=true, FATIGUE=true, MALAISE=true, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=true, VARICES=true, BILIRUBIN=false, SGOT=false, HISTOLOGY=false}), actualClassKind: live 
    26. isCorrect: true, result: Instance(classKind=live, featureNameToValue={AGE=true, FEMALE=false, STEROID=false, ANTIVIRALS=true, FATIGUE=false, MALAISE=false, ANOREXIA=true, BIGLIVER=true, FIRMLIVER=true, SPLEENPALPABLE=true, SPIDERS=true, ASCITES=false, VARICES=true, BILIRUBIN=true, SGOT=false, HISTOLOGY=true}), actualClassKind: live 


<!-- TODO  NEXT Compare the results of the baseline pacifier and the genie impurity one -->

The BaselineClassifier (always returns "live") had accuracy of 85%, while the
DecisionTree had an accuracy of 74%.

It is surprising to see that the accuracy is lower for the DecisionTree.
However, the BaselineClassifier's accuracy hints that the `hepatitis-test`
dataset has 85% `live` instances. There is not enough `die` instances (15%) to
properly test that the DecisionTree method works. 

The `hepatitis-training` dataset has 80% `live` instances and 20% `die` - only
22 `die` instances. Given that there are 16 features, and only 22 `die` training
instances, the DecisionTree cannot be trained properly. There is not enough
`die` data for the DecisionTree to be trained effectively.

The accuracy of the BaselineClassifier ideally should be closer to 50% (`1 /
numberOfDifferentClassKinds`) so training and testing can be more reliable.

## Q2


