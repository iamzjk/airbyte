

  create  table
    test_normalization.`conflict_stream_name____conflict_stream_name__dbt_tmp`
  as (
    
-- Final base SQL model
select
    _airbyte_conflict_stream_name_2_hashid,
    `groups`,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    
    CURRENT_TIMESTAMP
 as _airbyte_normalized_at,
    _airbyte_conflict_stream_name_3_hashid
from _airbyte_test_normalization.`conflict_stream_name__3flict_stream_name_ab3`
-- conflict_stream_name at conflict_stream_name/conflict_stream_name/conflict_stream_name from test_normalization.`conflict_stream_name_conflict_stream_name`
where 1 = 1
  )
