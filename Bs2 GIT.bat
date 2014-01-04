git add src\*.java
git status

set /p var="Commit Name: "
git commit -m "%var%"

git log
pause