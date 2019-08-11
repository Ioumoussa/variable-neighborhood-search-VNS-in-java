
import java.util.Random;

public class Tache {
	
	private static int nb_tache=0;
	
	private int id;
	private int periode;
	private int weight;
	private int due_date;
	Random rand = new Random(); 	
	
	public Tache() {
		super();
		Tache.nb_tache++;
		this.id =Tache.nb_tache;
		this.periode = rand.nextInt(1000) + 1;// 1 - 1000
		this.weight = rand.nextInt(10) + 1;;//1, - 10
		this.due_date = 0; /**/
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPeriode() {
		return periode;
	}
	public void setPeriode(int periode) {
		this.periode = periode;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getDue_date() {
		return due_date;
	}
	public void setDue_date(int due_date) {
		this.due_date = due_date;
	}
	public void generateDueDate(int sommePeriodes) {
		int borneInferieure = (int)((sommePeriodes)* 0.2);
		int borneSuperieure = (int)((sommePeriodes)* 0.6);
		this.setDue_date((int)(rand.nextInt(borneSuperieure) + borneInferieure));
		
	}
	

	
}
