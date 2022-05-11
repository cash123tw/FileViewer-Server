drop function if exists getReplacePath $$
create function getReplacePath(path varchar(500))
    returns varchar(500)
    deterministic
BEGIN
	DECLARE result VARCHAR(500);
    SET result = replace(path,'\\','/');
RETURN result;
END $$
-- --------------------------------------------------------
DROP FUNCTION  if EXISTS findChildId $$

create function findChildId(id INT)
    returns varchar(500)
    deterministic
BEGIN
    DECLARE ids VARCHAR(100);
    DECLARE tmp VARCHAR(100);
    SET tmp = id;

    while tmp is not NULL DO

        set ids = CONCAT_WS(',',ids,tmp);
    SELECT GROUP_CONCAT(f.id) INTO tmp FROM file_path f WHERE FIND_IN_SET(f.parent,tmp);

    END while;

    RETURN ids;
END $$
-- ------------------------------------------------
DROP PROCEDURE if EXISTS find_child_by_root $$

CREATE PROCEDURE `find_child_by_root`(
    IN `root` INT(100),
    IN `f_name` VARCHAR(100)
        )
BEGIN
SELECT f.*,l FROM(
                     SELECT @ids AS _ids,
                (SELECT (@ids := GROUP_CONCAT(id)) FROM file_path WHERE FIND_IN_SET(parent,@ids)) AS cid ,
                (SELECT @c:=@c+1	) AS l FROM file_path,
                         (SELECT @ids:= root, @c :=0) b
                 ) c ,file_path  f
WHERE _ids IS not NULL AND FIND_IN_SET(id,_ids) AND f.file_name LIKE CONCAT('%',f_name,'%')
ORDER BY path,l;
END $$