---
layout: site
title: Algorithms Reference Matrix Factorization
---
<!--
{% comment %}
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to you under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
{% endcomment %}
-->

## Principal Component Analysis

### PCA Description

Principal Component Analysis (PCA) is a simple, non-parametric method to
transform the given data set with possibly correlated columns into a set
of linearly uncorrelated or orthogonal columns, called *principal
components*. The principal components are ordered in such a way
that the first component accounts for the largest possible variance,
followed by remaining principal components in the decreasing order of
the amount of variance captured from the data. PCA is often used as a
dimensionality reduction technique, where the original data is projected
or rotated onto a low-dimensional space with basis vectors defined by
top-$K$ (for a given value of $K$) principal components.

#### PCA Details

Principal Component Analysis (PCA) is a non-parametric procedure for
orthogonal linear transformation of the input data to a new coordinate
system, such that the greatest variance by some projection of the data
comes to lie on the first coordinate (called the first principal
component), the second greatest variance on the second coordinate, and
so on. In other words, PCA first selects a normalized direction in
$m$-dimensional space ($m$ is the number of columns in the input data)
along which the variance in input data is maximized ??? this is referred
to as the first principal component. It then repeatedly finds other
directions (principal components) in which the variance is maximized. At
every step, PCA restricts the search for only those directions that are
perpendicular to all previously selected directions. By doing so, PCA
aims to reduce the redundancy among input variables. To understand the
notion of redundancy, consider an extreme scenario with a data set
comprising of two variables, where the first one denotes some quantity
expressed in meters, and the other variable represents the same quantity
but in inches. Both these variables evidently capture redundant
information, and hence one of them can be removed. In a general
scenario, keeping solely the linear combination of input variables would
both express the data more concisely and reduce the number of variables.
This is why PCA is often used as a dimensionality reduction technique.

The specific method to compute such a new coordinate system is as
follows ??? compute a covariance matrix $C$ that measures the strength of
correlation among all pairs of variables in the input data; factorize
$C$ according to eigen decomposition to calculate its eigenvalues and
eigenvectors; and finally, order eigenvectors in the decreasing order of
their corresponding eigenvalue. The computed eigenvectors (also known as
*loadings*) define the new coordinate system and the square
root of eigen values provide the amount of variance in the input data
explained by each coordinate or eigenvector.

#### PCA Returns

When MODEL is not provided, PCA procedure is
applied on INPUT data to generate MODEL as well as the rotated data
OUTPUT (if PROJDATA is set to $1$) in the new coordinate system. The
produced model consists of basis vectors MODEL$/dominant.eigen.vectors$
for the new coordinate system; eigen values
MODEL$/dominant.eigen.values$; and the standard deviation
MODEL$/dominant.eigen.standard.deviations$ of principal components. When
MODEL is provided, INPUT data is rotated according to the coordinate
system defined by MODEL$/dominant.eigen.vectors$. The resulting data is
stored at location OUTPUT.

* * *

## Matrix Completion via Alternating Minimizations

### MCAM Description

Low-rank matrix completion is an effective technique for statistical
data analysis widely used in the data mining and machine learning
applications. Matrix completion is a variant of low-rank matrix
factorization with the goal of recovering a partially observed and
potentially noisy matrix from a subset of its revealed entries. Perhaps
the most popular applications in which matrix completion has been
successfully applied is in the context of collaborative filtering in
recommender systems. In this setting, the rows in the data matrix
correspond to users, the columns to items such as movies, and entries to
feedback provided by users for items. The goal is to predict missing
entries of the rating matrix. This implementation uses the alternating
least-squares (ALS) technique for solving large-scale matrix completion
problems.

### MCAM Details

Given an $m \times n$ input matrix $V$ and a rank parameter
$r \ll \min{(m,n)}$, low-rank matrix factorization seeks to find an
$m \times r$ matrix $L$ and an $r \times n$ matrix $R$ such that
$V \approx LR$, i.e., we aim to approximate $V$ by the low-rank matrix
$LR$. The quality of the approximation is determined by an
application-dependent loss function $\mathcal{L}$. We aim at finding the
loss-minimizing factor matrices, i.e.,

$$
\begin{equation}
(L^*, R^*) = \textrm{argmin}_{L,R}{\mathcal{L}(V,L,R)}
\end{equation}
$$

In the
context of collaborative filtering in the recommender systems it is
often the case that the input matrix $V$ contains several missing
entries. Such entries are coded with the 0 value and the loss function
is computed only based on the nonzero entries in $V$, i.e.,

$$%\label{eq:loss}
 \mathcal{L}=\sum_{(i,j)\in\Omega}l(V_{ij},L_{i*},R_{*j})$$

where $L_{i\*}$ and $R_{\*j}$, respectively, denote the $i$th row of $L$ and the
$j$th column of $R$, $\Omega=\{\omega_1,\dots,\omega_N\}$ denotes the
training set containing the observed (nonzero) entries in $V$, and $l$
is some local loss function.

ALS is an optimization technique that can
be used to solve quadratic problems. For matrix completion, the
algorithm repeatedly keeps one of the unknown matrices ($L$ or $R$)
fixed and optimizes the other one. In particular, ALS alternates between
recomputing the rows of $L$ in one step and the columns of $R$ in the
subsequent step. Our implementation of the ALS algorithm supports the
loss functions summarized in [**Table 16**](#table16)
commonly used for matrix completion [[ZhouWSP08]](algorithms-bibliography.html).

#### Table 16

Popular loss functions supported by our ALS implementation; $N_{i\*}$
and $N_{\*j}$, respectively, denote the number of nonzero entries in
row $i$ and column $j$ of $V$.

| Loss                  | Definition |
| --------------------- | ---------- |
| $\mathcal{L}_\text{Nzsl}$     | $\sum_{i,j:V_{ij}\neq 0} (V_{ij} - [LR]_{ij})^2$
| $\mathcal{L}_\text{Nzsl+L2}$  | $\mathcal{L}_\text{Nzsl} + \lambda \Bigl( \sum_{ik} L_{ik}^2 + \sum_{kj} R_{kj}^2 \Bigr)$
| $\mathcal{L}_\text{Nzsl+wL2}$ | $\mathcal{L}_\text{Nzsl} + \lambda \Bigl(\sum_{ik}N_{i*} L_{ik}^2 + \sum_{kj}N_{*j} R_{kj}^2 \Bigr)$

* * *

Note that the matrix completion problem as defined in (1)
is a non-convex problem for all loss functions from
[**Table 16**](#table-16). However, when fixing one of the matrices
$L$ or $R$, we get a least-squares problem with a globally optimal
solution. For example, for the case of $$\mathcal{L}_\text{Nzsl+wL2}$$ we
have the following closed form solutions

$$\begin{aligned}
  L^\top_{n+1,i*} &\leftarrow (R^{(i)}_n {[R^{(i)}_n]}^\top + \lambda N_2 I)^{-1} R_n V^\top_{i*}, \\
  R_{n+1,*j} &\leftarrow ({[L^{(j)}_{n+1}]}^\top L^{(j)}_{n+1} + \lambda N_1 I)^{-1} L^\top_{n+1} V_{*j},
  \end{aligned}
$$

where $L_{n+1,i\*}$ (resp. $R_{n+1,\*j}$) denotes the
$i$th row of $L_{n+1}$ (resp. $j$th column of $R_{n+1}$), $\lambda$
denotes the regularization parameter, $I$ is the identity matrix of
appropriate dimensionality, $V_{i*}$ (resp. $V_{\*j}$) denotes the
revealed entries in row $i$ (column $j$), $R^{\(i\)}_n$ ( resp. 
$L^{(j)}_{n+1}$ ) refers to the corresponding columns of $R_n$ (rows of
$L_{n+1}$), and $N_1$ (resp. $N_2$) denotes a diagonal matrix that
contains the number of nonzero entries in row $i$ (column $j$) of $V$.

**Prediction.** Based on the factor matrices computed by ALS we provide
two prediction scripts:

  1. `ALS_predict.dml` computes the predicted ratings for a given
list of users and items.
  2. `ALS_topk_predict.dml` computes top-K item (where $K$ is
given as input) with highest predicted ratings together with their
corresponding ratings for a given list of users.

### MCAM Returns

We output the factor matrices $L$ and $R$ after the algorithm has
converged. The algorithm is declared as converged if one of the two
criteria is meet: (1) the decrease in the value of loss function falls
below `thr` given as an input parameter (if parameter
`check=TRUE`), or (2) the maximum number of iterations
(defined as parameter `maxi`) is reached. Note that for a
given user $i$ prediction is possible only if user $i$ has rated at
least one item, i.e., row $i$ in matrix $V$ has at least one nonzero
entry. In case, some users have not rated any items the corresponding
factor in $L$ will be all 0s. Similarly if some items have not been
rated at all the corresponding factors in $R$ will contain only 0s. Our
prediction scripts output the predicted ratings for a given list of
users and items as well as the top-K items with highest predicted
ratings together with the predicted ratings for a given list of users.
Note that the predictions will only be provided for the users who have
rated at least one item, i.e., the corresponding rows contain at least
one nonzero entry.
