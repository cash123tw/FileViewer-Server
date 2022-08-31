# --External Resources--

* [Inroduce Video](https://www.youtube.com/watch?v=5nUnsQeJJTg&t=0s)

* [Packaged File Download](https://drive.google.com/drive/folders/1nnkTsYp-WiSX1SuvMZePhjAAxKr7ADOh?usp=sharing)

# --System Configure--
* Java Version : `jdk-17`

* Relational database : `MySQL 8.0.28`

* Maven : `apache-maven-3.8.3`

* OS : `Windows 10`

* Using Browser : `Chrome`(PC,Phone),`Safari`
*(Because these browser is support `javascript ES6` module import)*
# --Using Framwork--
## Front End
* Vue.js
* Jquery.js
* Axios.js
* Bootstrap 5
## Back End
* Spring Boot 2.6.4
  >* MVC
  >* Web
  >* JPA
  >* Security
  >* Validation
  >* Thymeleaf
* Lombok
* Aspose-Words
* JWT

# --Service Introduce--
This service let you easy to manage your file.

For example : I have a directory tree like this,and record many personal information in different dir,personal information contains [`Age`,`Name`,`Location`,`Profession`,`Gender`,`...`].

Now if I want to find files whos `Location` is `Taiwan` and `Gender` is `Female`,it's too hard because file is scattered in different place.
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

# --Project Structure--
## Security Service
Use Spring Security to build security system,security class is all in [App.Security](https://github.com/cash123tw/FileViewer-Server/tree/main/src/main/java/App/Security)
,and mainly config class is [SecurityConfig](https://github.com/cash123tw/FileViewer-Server/blob/main/src/main/java/App/Security/SecurityConfig.java).
It's design to stateless,so when user first login,service will set a `JWT Cookie` include user's permission,username,name,
let user no need to login next time.
## App Initial Configuration
All config class is locate [App.Init](https://github.com/cash123tw/FileViewer-Server/tree/main/src/main/java/App/Init),
it will config needed `Bean` here,like hibernate function setting,System `start-mode`.
## Data Entity & Repository
All database related class locate [Data](https://github.com/cash123tw/FileViewer-Server/tree/main/src/main/java/Data)
,`ER Grapgh`(Entity Relation) is bottom of this page.
## Controller & Service
Locate in [App.Web](https://github.com/cash123tw/FileViewer-Server/tree/main/src/main/java/Web),
include request path controller and service,all handle logic is here. 

# --Service Init--
### Phase Test
Directory edit src/main/resources/[application.yml](https://github.com/cash123tw/FileViewer-Server/blob/main/src/main/resources/application.yml)
setting your local database [`username`,`password`,`URL`] at Spring.Datasource and setting [`scan-path`,`start-mode`] at file-explore.explore
### Phase Release
Package project to jar file,and add `set.yml` locate at same with jar file's directory,and setting [`username`,`password`,`URL`] at Spring.Datasource and setting [`scan-path`,`start-mode`] at file-explore.explore.

You can download already packaged file [here](https://drive.google.com/drive/folders/1nnkTsYp-WiSX1SuvMZePhjAAxKr7ADOh?usp=sharing)
### Custom param introduce
**file-explore.explore.scan-path** : Base scan path,service will scan all directory and file under this path.

**file-explore.explore.start-mode** : Care to setting this option,it will specify what service scan mode at started,3 type.
* `new` : Reset all data or tag,create new one. (Care to use if already have data in database,this option will delete all data,and renew database)
* `rescan` : Default setting,scan `scan-path` again compare database file's data with local file,if file is missing it will delete file's data in database,else if file is not record in database,it will add new file's data in database automatic.
* `none` : do nothin at service start.

# --Service First Start--
First start service will take a little time to init database and add file data,depend on how many file in your `scan-path`
,and when service started,will print line in terminal`URL : [http://192.168.0.80:9090]`,*(this is my service info,not same as your)*,now you can use your PC direct this url,and if you are not login,it will redirect to login page,
look at service terminal have another line `Root user registed username:[root] password:[root]`,
this is used to login service when system first start.
 
Also can login with your phone,but your phone `network area` need same with service computer's `Local Network Area`.

# --Service Introduce--
## 1.Authentication
All service function need to login,and user account need to have different **permission** to do different thing,
all account have permission `WATCH`,if want to add more permission like `ADMIN` or `EDIT`, need user who have permission 
`ADMIN` to authorize.

###Permission Type
* `ADMIN` : Decide user status enable or not when register,and can authorize permission to user.
* `EDIT` : Add or edit `tag`,`tag type` and `file data` to service. 
* `WATCH` : Read all `file`,`tag`,`tag type`,all user have this permission.
## 2.File Data
File data explore service,You can view all file here,and add new directory or file to service,
and if want to find specify `TAG`,`FILE_NAME` to search,you can use search file function. 
## 3.Tag
View all `TAG` in tag service,also can add or edit.   
## 4.Tag Type
View all `TAG Type` in tag type service,also can add,edit and view which tag related to this Tag Type.
## 5.Admin User Manage
You can enter manage service if you have `ADMIN` permission,you can manage all user account here,
like user enable status or user's permission.
## 6.User Detail
You can check your account detail,like username,name,permission...,and you can change your password here.
# --Data Entity ER Graph--
Make reader easy to understand service's data entity design.

## Main Data Graph
File data record structure in database.
![Main Data](https://github.com/cash123tw/FileViewer-Server/blob/main/README_LIB/Main%20Data%20ER%20Graph.JPG)
## Security Data Graph
Authentication service data structure in database.
![UserInfo Data](https://github.com/cash123tw/FileViewer-Server/blob/main/README_LIB/UserInfo%20Data%20ER%20Graph.jpg)
