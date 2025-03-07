{{ config(
    indexes = [{'columns':['_airbyte_emitted_at'],'type':'hash'}],
    unique_key = env_var('AIRBYTE_DEFAULT_UNIQUE_KEY', '_airbyte_ab_id'),
    schema = "test_normalization",
    tags = [ "top-level" ]
) }}
-- Final base SQL model
select
    {{ adapter.quote('id') }},
    conflict_stream_scalar,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    {{ current_timestamp() }} as _airbyte_normalized_at,
    _airbyte_conflict_stream_scalar_hashid
from {{ ref('conflict_stream_scalar_ab3') }}
-- conflict_stream_scalar from {{ source('test_normalization', '_airbyte_raw_conflict_stream_scalar') }}
where 1 = 1

