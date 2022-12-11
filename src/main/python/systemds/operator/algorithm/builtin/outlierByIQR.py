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
# Autogenerated From : scripts/builtin/outlierByIQR.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def outlierByIQR(X: Matrix,
                 k: float,
                 max_iterations: int,
                 **kwargs: Dict[str, VALID_INPUT_TYPES]):
    """
     Builtin function for detecting and repairing outliers using standard deviation 
    
    
    
    :param X: Matrix X
    :param k: a constant used to discern outliers k*IQR
    :param isIterative: iterative repair or single repair
    :param repairMethod: values: 0 = delete rows having outliers,
        1 = replace outliers with zeros
        2 = replace outliers as missing values
    :param max_iterations: values: 0 = arbitrary number of iteraition until all outliers are removed,
        n = any constant defined by user
    :param verbose: flag specifying if logging information should be printed
    :return: Matrix X with no outliers
    """

    params_dict = {'X': X, 'k': k, 'max_iterations': max_iterations}
    params_dict.update(kwargs)
    
    vX_0 = Matrix(X.sds_context, '')
    vX_1 = Matrix(X.sds_context, '')
    vX_2 = Matrix(X.sds_context, '')
    vX_3 = Matrix(X.sds_context, '')
    vX_4 = Scalar(X.sds_context, '')
    vX_5 = Scalar(X.sds_context, '')
    output_nodes = [vX_0, vX_1, vX_2, vX_3, vX_4, vX_5, ]

    op = MultiReturn(X.sds_context, 'outlierByIQR', output_nodes, named_input_nodes=params_dict)

    vX_0._unnamed_input_nodes = [op]
    vX_1._unnamed_input_nodes = [op]
    vX_2._unnamed_input_nodes = [op]
    vX_3._unnamed_input_nodes = [op]
    vX_4._unnamed_input_nodes = [op]
    vX_5._unnamed_input_nodes = [op]

    return op
