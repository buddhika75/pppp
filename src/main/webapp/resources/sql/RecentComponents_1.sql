select * from solution where name like "%DHIS%";
select * from solutionevaluationschema where `SOLUTION_ID` = 2967;
select * from solutionevaluationgroup WHERE `SOLUTIONEVALUATIONSCHEME_ID` in (2968,3126,3187);
select * from solutionevaluationitem where `SOLUTIONEVALUATIONGROUP_ID` in (2969,3127,3188);
select count(*) FROM solutionitem  group by SOLUTIONEVALUATIONITEM_ID;