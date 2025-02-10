# genealogicalTree
Program to store your family members

# Setup:
In a directory *'src/main/resources/secrets/'* you need to create two text files.
*'dbUsername'* with your PostgreSQL username &
*'dbPassword'* with your PostgreSQL password. You don't need to create a database itself.<br>
It will be crated automatically with a name from *'dbName'* file.
<br>
In a file *'dbUrl'* is a default URL of the PostgreSQL database server with default port 5432. If on your machine PostgreSQL server is on not default port, you need to change *'dbUrl'* file.