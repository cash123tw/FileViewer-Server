## [Inroduce Video](https://)

# --System Configure--
Java Version : `jdk-17`

Relational database : `MySQL 8.0`

Maven : `apache-maven-3.8.3`

# --Service Introduce--
This service let you easy to manage your file

For example : I have a directory tree like this,and record many personal information in different dir,personal information contains [`Age`,`Name`,`Location`,`Profession`,`Gender`,`...`].

Now if I want to finding files whos `Location` is `Taiwan` and `Gender` is `Female`,it's too hard because file is scattered in different place.
And this service is design to solve this problem by adding `TAG` to file,adding `TAG` won't change original file data or directory tree structure,it's record in MySQL database `TAG` table.


* Dir_A
    * Dir_A-1
      >+ File_1 *Content : Mary's Info Age:18,Gender:FeMale,Location:Taiwan*
      >- File_2 *Content : Jay's Info Age:40,Gender:Male,Location:USA*
    * Dir_A-2
      >+ File_3 *Content : Martin's Info Age:62,Gender:FeMale,Location:China*
      >+ File_4 *Content : Arisa's Info Age:25,Gender:FeMale,Location:Taiwan*
* Dir_B
    * Dir_B-1
        * Dir_B-1-1
          >+ File_5 *Content : Jessi's Info Age:22,Gender:FeMale,Location:Taiwan*
          >+ File_6 *Content : Loria's Info Age:30,Gender:FeMale,Location:USA*
          >+ File_7 *Content : Dennial's Info Age:25,Gender:Male,Location:Taiwan*
    * Dir_B-2
      >+ File_8 *Content : Tenz's Info Age:18,Gender:Male,Location:China*
      >+ File_9 *Content : Henry's Info Age:11,Gender:Male,Location:Japan*

# --Service Init--
### Phase Test
Directory edit src/main/resources/[application.yml](https://github.com/cash123tw/FileViewer-Server/blob/main/src/main/resources/application.yml)
setting your local database [`username`,`password`,`URL`] at Spring.Datasource and setting [`scan-path`,`start-mode`] at file-explore.explore
### Phase Release
Package project to jar file,and add `Setting.yml` locate at same with jar file's directory,and setting [`username`,`password`,`URL`] at Spring.Datasource and setting [`scan-path`,`start-mode`] at file-explore.explore.
### Custom param introduce
**file-explore.explore.scan-path** : Base scan path,service will scan all direcotry and file under this path.

**file-explore.explore.start-mode** : Care to setting this option,it will specify what service scan mode at started,3 type.
* `new` : Reset all data or tag,create new one. (Care to use if already have data in database,this option will delete all data,and renew database)
* `rescan` : Default setting,scan `scan-path` again compare database file's data with local file,if file is missing it will delete file's data in database,else if file is not record in database,it will add new file's data in database automatic.
* `none` : do nothin at service start.

# --Data Entity ER Graph--
Make reader easy to understand service's data entity design.

## Main Data Graph
File data record structure in database.
![Main Data](https://github.com/cash123tw/FileViewer-Server/blob/main/README_LIB/Main%20Data%20ER%20Graph.JPG)
## Security Data Graph
Authentication service data structure in database.
![UserInfo Data](https://github.com/cash123tw/FileViewer-Server/blob/main/README_LIB/UserInfo%20Data%20ER%20Graph.jpg)