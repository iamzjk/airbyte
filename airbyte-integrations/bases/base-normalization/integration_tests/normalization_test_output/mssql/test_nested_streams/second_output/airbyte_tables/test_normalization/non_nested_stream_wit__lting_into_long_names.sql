
   
  USE [test_normalization];
  if object_id ('test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp_temp_view"','V') is not null
      begin
      drop view test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp_temp_view"
      end


   
   
  USE [test_normalization];
  if object_id ('test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp"','U') is not null
      begin
      drop table test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp"
      end


   USE [test_normalization];
   EXEC('create view test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp_temp_view" as
    
-- Final base SQL model
select
    id,
    "date",
    _airbyte_ab_id,
    _airbyte_emitted_at,
    SYSDATETIME() as _airbyte_normalized_at,
    _airbyte_non_nested_s__nto_long_names_hashid
from "test_normalization"._airbyte_test_normalization."non_nested_stream_wit__lting_into_long_names_ab3"
-- non_nested_stream_wit__lting_into_long_names from "test_normalization".test_normalization._airbyte_raw_non_nested_stream_without_namespace_resulting_into_long_names
where 1 = 1
    ');

   SELECT * INTO "test_normalization".test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp" FROM
    "test_normalization".test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp_temp_view"

   
   
  USE [test_normalization];
  if object_id ('test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp_temp_view"','V') is not null
      begin
      drop view test_normalization."non_nested_stream_wit__lting_into_long_names__dbt_tmp_temp_view"
      end

    
   use [test_normalization];
  if EXISTS (
        SELECT * FROM
        sys.indexes WHERE name = 'test_normalization_non_nested_stream_wit__lting_into_long_names__dbt_tmp_cci'
        AND object_id=object_id('test_normalization_non_nested_stream_wit__lting_into_long_names__dbt_tmp')
    )
  DROP index test_normalization.non_nested_stream_wit__lting_into_long_names__dbt_tmp.test_normalization_non_nested_stream_wit__lting_into_long_names__dbt_tmp_cci
  CREATE CLUSTERED COLUMNSTORE INDEX test_normalization_non_nested_stream_wit__lting_into_long_names__dbt_tmp_cci
    ON test_normalization.non_nested_stream_wit__lting_into_long_names__dbt_tmp

   

