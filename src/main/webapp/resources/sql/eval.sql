select id,name from solution order by id desc limit 10;
select id,`NAME` from evaluationschema order by id desc limit 10;
select id,name,`EVALUATIONSCHEMA_ID` from evaluationgroup order by id desc limit 10;
select id,`NAME`,`EVALUATIONGROUP_ID` from evaluationitem order by id desc limit 10;
select id,`CREATEDAT`,`SOLUTION_ID`,`EVALUATIONSCHEMA_ID` from solutionevaluationschema order  by id desc limit 10;
select id,`CREATEDAT`,`SOLUTIONEVALUATIONSCHEME_ID` from solutionevaluationgroup order by id desc limit 10;
select `ID`,`CREATEDAT`,`SOLUTIONEVALUATIONGROUP_ID` from solutionevaluationitem order by id desc limit 10;
select `ID`,`CREATEDAT`,`SOLUTIONEVALUATIONITEM_ID`,`SHORTTEXTVALUE` from solutionitem order by id desc limit 10;


