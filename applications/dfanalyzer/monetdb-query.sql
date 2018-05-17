select * from data_transformation;

SELECT * FROM data_set;

SELECT * FROM extractor;

SELECT * FROM data_dependency;

SELECT prev_dt.tag as previous_transformation, next_dt.tag as next_transformation, ds.tag as dataset
FROM data_dependency dep, data_transformation prev_dt, data_transformation next_dt, data_set ds
WHERE dep.previous_dt_id = prev_dt.id
AND dep.next_dt_id = next_dt.id
AND dep.ds_id = ds.id;

SELECT * FROM extractor;

SELECT * FROM attribute;

SELECT a.name, a.type, s.tag, a.extractor_id FROM attribute a, data_set s WHERE a.ds_id=s.id ;

SELECT dt.tag, t.* FROM task t, data_transformation dt WHERE t.dt_id = dt.id;

SELECT * FROM iinit_mesh;

SELECT * FROM oinit_mesh;

SELECT * FROM icreate_equation_systems;

SELECT * FROM ocreate_equation_systems;

SELECT * FROM isolve_equation_systems;

SELECT * FROM osolve_equation_systems;

SELECT * FROM owrite_mesh;

SELECT * FROM oextract_data;

SELECT * FROM ext_extractor;

SELECT * FROM ideduplication;

SELECT * FROM odeduplication;

SELECT * FROM "ext_ext_oeurope";

SELECT * FROM oeurope;

