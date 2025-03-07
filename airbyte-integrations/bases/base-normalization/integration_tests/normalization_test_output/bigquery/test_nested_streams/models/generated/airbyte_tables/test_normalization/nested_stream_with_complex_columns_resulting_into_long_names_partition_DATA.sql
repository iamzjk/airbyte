{{ config(
    cluster_by = "_airbyte_emitted_at",
    partition_by = {"field": "_airbyte_emitted_at", "data_type": "timestamp", "granularity": "day"},
    unique_key = env_var('AIRBYTE_DEFAULT_UNIQUE_KEY', '_airbyte_ab_id'),
    schema = "test_normalization",
    tags = [ "nested" ]
) }}
-- Final base SQL model
select
    _airbyte_partition_hashid,
    currency,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    {{ current_timestamp() }} as _airbyte_normalized_at,
    _airbyte_DATA_hashid
from {{ ref('nested_stream_with_complex_columns_resulting_into_long_names_partition_DATA_ab3') }}
-- DATA at nested_stream_with_complex_columns_resulting_into_long_names/partition/DATA from {{ ref('nested_stream_with_complex_columns_resulting_into_long_names_partition') }}
where 1 = 1

