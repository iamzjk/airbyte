{{ config(
    sort = "_airbyte_emitted_at",
    unique_key = env_var('AIRBYTE_DEFAULT_UNIQUE_KEY', '_airbyte_ab_id'),
    schema = "_airbyte_test_normalization",
    tags = [ "nested-intermediate" ]
) }}
-- SQL model to parse JSON blob stored in a single column and extract into separated field columns as described by the JSON Schema
{{ unnest_cte('unnest_alias', 'unnest_alias', 'children') }}
select
    _airbyte_unnest_alias_hashid,
    {{ json_extract_scalar(unnested_column_value('children'), ['ab_id'], ['ab_id']) }} as ab_id,
    {{ json_extract('', unnested_column_value('children'), ['owner'], ['owner']) }} as owner,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    {{ current_timestamp() }} as _airbyte_normalized_at
from {{ ref('unnest_alias') }} as table_alias
-- children at unnest_alias/children
{{ cross_join_unnest('unnest_alias', 'children') }}
where 1 = 1
and children is not null

