# -------------------------------------------------------------
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# -------------------------------------------------------------

# Autogenerated By   : src/main/python/generator/generator.py
# Autogenerated From : scripts/builtin/glm.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def glm(X: Matrix,
        Y: Matrix,
        **kwargs: Dict[str, VALID_INPUT_TYPES]):
    """
     This script solves GLM regression using NEWTON/FISHER scoring with trust regions. The glm-function is a flexible
     generalization of ordinary linear regression that allows for response variables that have error distribution models.
    
     In addition, some GLM statistics are provided as console output by setting verbose=TRUE, one comma-separated name-value
     pair per each line, as follows:
    
     .. code-block::
    
       --------------------------------------------------------------------------------------------
       TERMINATION_CODE      A positive integer indicating success/failure as follows:
                             1 = Converged successfully; 2 = Maximum number of iterations reached; 
                             3 = Input (X, Y) out of range; 4 = Distribution/link is not supported
       BETA_MIN              Smallest beta value (regression coefficient), excluding the intercept
       BETA_MIN_INDEX        Column index for the smallest beta value
       BETA_MAX              Largest beta value (regression coefficient), excluding the intercept
       BETA_MAX_INDEX        Column index for the largest beta value
       INTERCEPT             Intercept value, or NaN if there is no intercept (if icpt=0)
       DISPERSION            Dispersion used to scale deviance, provided as "disp" input parameter
                             or estimated (same as DISPERSION_EST) if the "disp" parameter is <= 0
       DISPERSION_EST        Dispersion estimated from the dataset
       DEVIANCE_UNSCALED     Deviance from the saturated model, assuming dispersion == 1.0
       DEVIANCE_SCALED       Deviance from the saturated model, scaled by the DISPERSION value
       --------------------------------------------------------------------------------------------
       
       The Log file, when requested, contains the following per-iteration variables in CSV format,
       each line containing triple (NAME, ITERATION, VALUE) with ITERATION = 0 for initial values:
       
       --------------------------------------------------------------------------------------------
       NUM_CG_ITERS          Number of inner (Conj.Gradient) iterations in this outer iteration
       IS_TRUST_REACHED      1 = trust region boundary was reached, 0 = otherwise
       POINT_STEP_NORM       L2-norm of iteration step from old point (i.e. "beta") to new point
       OBJECTIVE             The loss function we minimize (i.e. negative partial log-likelihood)
       OBJ_DROP_REAL         Reduction in the objective during this iteration, actual value
       OBJ_DROP_PRED         Reduction in the objective predicted by a quadratic approximation
       OBJ_DROP_RATIO        Actual-to-predicted reduction ratio, used to update the trust region
       GRADIENT_NORM         L2-norm of the loss function gradient (NOTE: sometimes omitted)
       LINEAR_TERM_MIN       The minimum value of X %*% beta, used to check for overflows
       LINEAR_TERM_MAX       The maximum value of X %*% beta, used to check for overflows
       IS_POINT_UPDATED      1 = new point accepted; 0 = new point rejected, old point restored
       TRUST_DELTA           Updated trust region size, the "delta"
       --------------------------------------------------------------------------------------------
    
     SOME OF THE SUPPORTED GLM DISTRIBUTION FAMILIES
     AND LINK FUNCTIONS:
    
     .. code-block::
    
       dfam vpow link lpow  Distribution.link   nical?
       ---------------------------------------------------
        1   0.0   1  -1.0   Gaussian.inverse
        1   0.0   1   0.0   Gaussian.log
        1   0.0   1   1.0   Gaussian.id          Yes
        1   1.0   1   0.0   Poisson.log          Yes
        1   1.0   1   0.5   Poisson.sqrt
        1   1.0   1   1.0   Poisson.id
        1   2.0   1  -1.0   Gamma.inverse        Yes
        1   2.0   1   0.0   Gamma.log
        1   2.0   1   1.0   Gamma.id
        1   3.0   1  -2.0   InvGaussian.1/mu^2   Yes
        1   3.0   1  -1.0   InvGaussian.inverse
        1   3.0   1   0.0   InvGaussian.log
        1   3.0   1   1.0   InvGaussian.id
        1    *    1    *    AnyVariance.AnyLink
       ---------------------------------------------------
        2    *    1   0.0   Binomial.log
        2    *    1   0.5   Binomial.sqrt
        2    *    2    *    Binomial.logit       Yes
        2    *    3    *    Binomial.probit
        2    *    4    *    Binomial.cloglog
        2    *    5    *    Binomial.cauchit
       ---------------------------------------------------
    
    
    
    :param X: matrix X of feature vectors
    :param Y: matrix Y with either 1 or 2 columns:
        if dfam = 2, Y is 1-column Bernoulli or 2-column Binomial (#pos, #neg)
    :param dfam: Distribution family code: 1 = Power, 2 = Binomial
    :param vpow: Power for Variance defined as (mean)^power (ignored if dfam != 1):
        0.0 = Gaussian, 1.0 = Poisson, 2.0 = Gamma, 3.0 = Inverse Gaussian
    :param link: Link function code: 0 = canonical (depends on distribution),
        1 = Power, 2 = Logit, 3 = Probit, 4 = Cloglog, 5 = Cauchit
    :param lpow: Power for Link function defined as (mean)^power (ignored if link != 1):
        -2.0 = 1/mu^2, -1.0 = reciprocal, 0.0 = log, 0.5 = sqrt, 1.0 = identity
    :param yneg: Response value for Bernoulli "No" label, usually 0.0 or -1.0
    :param icpt: Intercept presence, X columns shifting and rescaling:
        0 = no intercept, no shifting, no rescaling;
        1 = add intercept, but neither shift nor rescale X;
        2 = add intercept, shift & rescale X columns to mean = 0, variance = 1
    :param reg: Regularization parameter (lambda) for L2 regularization
    :param tol: Tolerance (epsilon)
    :param disp: (Over-)dispersion value, or 0.0 to estimate it from data
    :param moi: Maximum number of outer (Newton / Fisher Scoring) iterations
    :param mii: Maximum number of inner (Conjugate Gradient) iterations, 0 = no maximum
    :param verbose: if the Algorithm should be verbose
    :return: Matrix beta, whose size depends on icpt:
        icpt=0: ncol(X) x 1;  icpt=1: (ncol(X) + 1) x 1;  icpt=2: (ncol(X) + 1) x 2
    """

    params_dict = {'X': X, 'Y': Y}
    params_dict.update(kwargs)
    return Matrix(X.sds_context,
        'glm',
        named_input_nodes=params_dict)
