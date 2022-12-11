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
# Autogenerated From : scripts/builtin/kmeans.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode, Matrix, Frame, List, MultiReturn, Scalar
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES


def kmeans(X: Matrix,
           **kwargs: Dict[str, VALID_INPUT_TYPES]):
    """
     Builtin function that implements the k-Means clustering algorithm
    
    
    
    :param X: The input Matrix to do KMeans on.
    :param k: Number of centroids
    :param runs: Number of runs (with different initial centroids)
    :param max_iter: Maximum number of iterations per run
    :param eps: Tolerance (epsilon) for WCSS change ratio
    :param is_verbose: do not print per-iteration stats
    :param avg_sample_size_per_centroid: Average number of records per centroid in data samples
    :param seed: The seed used for initial sampling. If set to -1
        random seeds are selected.
    :return: The mapping of records to centroids
    :return: The output matrix with the centroids
    """

    params_dict = {'X': X}
    params_dict.update(kwargs)
    
    vX_0 = Matrix(X.sds_context, '')
    vX_1 = Matrix(X.sds_context, '')
    output_nodes = [vX_0, vX_1, ]

    op = MultiReturn(X.sds_context, 'kmeans', output_nodes, named_input_nodes=params_dict)

    vX_0._unnamed_input_nodes = [op]
    vX_1._unnamed_input_nodes = [op]

    return op