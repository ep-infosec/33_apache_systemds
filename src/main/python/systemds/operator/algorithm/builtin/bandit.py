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
# Autogenerated From : scripts/builtin/bandit.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def bandit(X_train: Matrix,
           Y_train: Matrix,
           X_test: Matrix,
           Y_test: Matrix,
           metaList: List,
           evaluationFunc: str,
           evalFunHp: Matrix,
           lp: Frame,
           lpHp: Matrix,
           primitives: Frame,
           param: Frame,
           baseLineScore: float,
           cv: bool,
           **kwargs: Dict[str, VALID_INPUT_TYPES]):
    """
     In The bandit function the objective is to find an arm that optimizes
     a known functional of the unknown arm-reward distributions.
    
    
    
    :param X_train: ---
    :param Y_train: ---
    :param X_test: ---
    :param Y_test: ---
    :param metaList: ---
    :param evaluationFunc: ---
    :param evalFunHp: ---
    :param lp: ---
    :param primitives: ---
    :param params: ---
    :param K: ---
    :param R: ---
    :param baseLineScore: ---
    :param cv: ---
    :param cvk: ---
    :param verbose: ---
    :param output: ---
    :return: ---
    """

    params_dict = {'X_train': X_train, 'Y_train': Y_train, 'X_test': X_test, 'Y_test': Y_test, 'metaList': metaList, 'evaluationFunc': evaluationFunc, 'evalFunHp': evalFunHp, 'lp': lp, 'lpHp': lpHp, 'primitives': primitives, 'param': param, 'baseLineScore': baseLineScore, 'cv': cv}
    params_dict.update(kwargs)
    
    vX_0 = Frame(X_train.sds_context, '')
    vX_1 = Matrix(X_train.sds_context, '')
    vX_2 = Matrix(X_train.sds_context, '')
    vX_3 = Frame(X_train.sds_context, '')
    output_nodes = [vX_0, vX_1, vX_2, vX_3, ]

    op = MultiReturn(X_train.sds_context, 'bandit', output_nodes, named_input_nodes=params_dict)

    vX_0._unnamed_input_nodes = [op]
    vX_1._unnamed_input_nodes = [op]
    vX_2._unnamed_input_nodes = [op]
    vX_3._unnamed_input_nodes = [op]

    return op
