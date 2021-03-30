<div style="font-size: 40px; font-weight: bold;text-align: center;">Bookmark Tomb</div>

> This is a bookmark sync system. Now, you locate at backend project.

You can go to our [homepage](https://bookmarktomb.github.io/BookmarkTomb_Docs) for more help.

## Simple introduction

The backend project build with SpringBoot and MongoDB. The project need java(>=11) to run.

The details as fallow table: 

|Function|Main Technique|
|:----:|:----:|
|Framework|SpringBoot|
|Database|MongoDB|
|Authentication|Spring Security with Token|
|API Document|Swagger with [knife4j](https://github.com/xiaoymin/swagger-bootstrap-ui)|

## Dev and Debug

1. Clone the project
   
    `git clone https://mygitea.fallen-angle.com/fallen-angle/bookmark_tomb_back`
   
2. Import to IDEA or other IDE
3. Set develop java version to 11 or higher.
4. Wait a minute for pull dependencies from maven repository.
5. At last, you can write configure by [url or file](https://bookmarktomb.github.io/BookmarkTomb_Docs/#develop).

## Others

This project is unexpect, many things can be improved. Such as: 

- Use `ChangeStream` instead of `Java Code` deal with some pretreatment;
- Can simplify the DTO and VO names.
- Maybe MySQL is better for the project.
- And so on ...

So, the project will refactor soon. Maybe at last version.