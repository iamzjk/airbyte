{{ config(
    sort = ["_airbyte_unique_key", "_airbyte_emitted_at"],
    unique_key = "_airbyte_unique_key",
    schema = "test_normalization",
    tags = [ "top-level" ]
) }}
-- Final base SQL model
select
    _airbyte_unique_key,
    id,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    {{ current_timestamp() }} as _airbyte_normalized_at,
    _airbyte_renamed_dedup_cdc_excluded_hashid
from {{ ref('renamed_dedup_cdc_excluded_scd') }}
-- renamed_dedup_cdc_excluded from {{ source('test_normalization', '_airbyte_raw_renamed_dedup_cdc_excluded') }}
where 1 = 1
and _airbyte_active_row = 1
{{ incremental_clause('_airbyte_emitted_at') }}

