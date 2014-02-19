package Freeman.fetch;

public abstract class FetchArticle 
{
	abstract public Article getArticle(); 
	abstract void parseTitle();
	abstract void parseContent();
	abstract void parsePush();
}
