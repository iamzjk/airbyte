{{ config(
    cluster_by = "_airbyte_emitted_at",
    partition_by = {"field": "_airbyte_emitted_at", "data_type": "timestamp", "granularity": "day"},
    unique_key = env_var('AIRBYTE_DEFAULT_UNIQUE_KEY', '_airbyte_ab_id'),
    schema = "_airbyte_test_normalization",
    tags = [ "top-level-intermediate" ]
) }}
-- SQL model to parse JSON blob stored in a single column and extract into separated field columns as described by the JSON Schema
select
    {{ json_extract_scalar('_airbyte_data', ['id'], ['id']) }} as id,
    {{ json_extract_scalar('_airbyte_data', ['date'], ['date']) }} as date,
    {{ json_extract('table_alias', '_airbyte_data', ['partition'], ['partition']) }} as {{ adapter.quote('partition') }},
    _airbyte_ab_id,
    _airbyte_emitted_at,
    {{ current_timestamp() }} as _airbyte_normalized_at
from {{ source('test_normalization', '_airbyte_raw_nested_stream_with_complex_columns_resulting_into_long_names') }} as table_alias
-- nested_stream_with_complex_columns_resulting_into_long_names
where 1 = 1
{{ incremental_clause('_airbyte_emitted_at') }}

