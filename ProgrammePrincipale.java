
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class ProgrammePrincipale {
	
	static List<Tache> taches = new ArrayList<Tache>();
	static int tache_number=0;
	static int[] valeursInitiales;
	static boolean affichierCalc=true;
	/*
	 * Tableaux des valeurs
	 */
	static int[] pour_periode, pour_weight, pour_dueDate;
	/*
	 * Matrice de correspondance
	 */
	static HashMap<Integer, HashMap> mC = new HashMap<Integer, HashMap>();
	/*
	 * Ordonencement optimal
	 */
	static ArrayList<Integer> ordonencement = new ArrayList<Integer>();
	
	
	public synchronized static void affichageInitiale(int sommePeriodes) {
		/*
		 * Affichage des values des taches (weight, periode, due date)
		 */
		
		for (int i = 0; i < taches.size(); i++) {
			
			System.out.println("-Tache "+ (taches.get(i).getId()));
			System.out.println("Periode  : " + taches.get(i).getPeriode());
			System.out.println("Weight   : " + taches.get(i).getWeight());
			System.out.println("Due date : " + taches.get(i).getDue_date());
			
			System.out.println();
			System.out.println();
		}
		
		/*
		 * Affichage de la somme des periodes des taches
		 */
		System.out.println("Somme des periodes :  " + sommePeriodes);
	}
	
	public synchronized static void calcValeursInitiales() {
		/*
		 * Calcule valeurs initiales
		 */
		 
	    valeursInitiales = new int[tache_number];

		int weight=0, periode=0, dueDate=0;
		for (int i = 0; i < taches .size(); i++) {
			periode = taches.get(i).getPeriode();
		  	dueDate = taches.get(i).getDue_date();
		  	weight = taches.get(i).getWeight();
			
		  	valeursInitiales[i]= weight * max(0,(periode-dueDate));
		}
		

		int nbTachesCourantes = 1;
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

	   	/*
	   	 * Affichage m1
	   	 */
	    
	    System.out.println("Valeurs pour 1 tache");
	    for(int i = 0; i < valeursInitiales.length; i++) {
	    	System.out.printf("f(%d)=%d\n", (i+1), valeursInitiales[i]);
	    }
	    
	}
	
	public synchronized static void calcValeurOptimaleFonction() {
		/*
		 * Calcule valeur optimale de la fonction objectif
		 */
		int valeurOptimaleFonction = 0;
		int weight=0, periode=0, dueDate=0, sommePeriode = 0;
		for (int i = 0; i < taches .size(); i++) {
			periode = taches.get(i).getPeriode();
		  	dueDate = taches.get(i).getDue_date();
		  	weight = taches.get(i).getWeight();
			sommePeriode += periode;
			valeurOptimaleFonction = weight * max(0,(sommePeriode-dueDate));
		}

		/*
		 * Affichage valeur optimale de la fonction objectif
		 */
		System.out.println("Valeur optimale de la fonction objective : "+  valeurOptimaleFonction);
	}
	
	
	public synchronized static void definitionTableauxValeurs() {
		/*
		 * Enregistrer les valeurs des taches(weight, periode, due date) dans des tableaux specifiques
		 */
		pour_periode = new int[tache_number];
		pour_weight = new int[tache_number];
		pour_dueDate = new int[tache_number];
		for( int i=0 ; i < taches.size() ; i++) {
			pour_periode[i] = taches.get(i).getPeriode();
			pour_weight[i] = taches.get(i).getWeight();
			pour_dueDate[i] = taches.get(i).getDue_date();
		}
	}
	
	public synchronized static void calcM2() {
		/*
		 * Calcule m2
		 */
		
		HashMap<String, int[]> m2 = new HashMap<String, int[]>();
	   
		for(int i = 1; i < tache_number; i++) {
			for(int j = i+1; j < tache_number+1; j++) {
				int x1 = 0, x2 = 0;
				
				x1 = valeursInitiales[i-1] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1] - pour_dueDate[j-1])));
				x2 = valeursInitiales[j-1] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1] - pour_dueDate[i-1])));
			   
				int[] values = new int[2];
				if(x1 >= x2) {
					values = new int[]{j, x2};
				} else {
					values = new int[]{i, x1};
				}
				
				String keys = TabToString(new int[]{i, j});
				m2.put(keys, values);
			}
		}

		/*
		 * Correspondre la m2 avec son indice
		 */
		int nbTachesCourantes = 2;
		mC.put(nbTachesCourantes, m2);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

	   /*
	    * Affichage m2
	    */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 2 taches");
		for(int i = 1; i < tache_number; i++) {
			for(int j = i+1; j < tache_number+1; j++) {
				String keys = TabToString(new int[]{i, j});
				System.out.printf("f({%d, %d}) = [%d, %d]\n", i, j, m2.get(keys)[0], m2.get(keys)[1]);
			}
		}
		
	}
	
	public synchronized static void calcM3() {
		/*
		 * Calcule m3
		 */
		HashMap<String, int[]> m2 = mC.get(2);
		HashMap<String, int[]> m3 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-1; i++) {
			for(int j = i+1; j < tache_number; j++ ) {
				for(int k = j+1; k < tache_number+1; k++) {
					int x1 = 0, x2 = 0, x3 = 0;
				   
					String keys = TabToString(new int[]{i, j});
					x1 = m2.get(keys)[1] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1] - pour_dueDate[k-1])));
					keys = TabToString(new int[]{i, k});
					x2 = m2.get(keys)[1] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1] - pour_dueDate[j-1])));
					keys = TabToString(new int[]{j, k});
					x3 = m2.get(keys)[1] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1] - pour_dueDate[i-1])));
					
					int[] values = new int[3];
					if(x1 >= x3 && x2 >= x3) {
						values = new int[]{j, k, x3};
					} else if(x1 >= x2 && x3 >= x2) {
						values = new int[]{i, k, x2};
					} else if(x2 >= x1 && x3 >= x1) {
						values = new int[]{i, j, x1};
					}
					keys = TabToString(new int[]{i, j, k});
					m3.put(keys, values);
				}
			}
		}

		/*
		 * Correspondre la m3 avec son indice
		 */
		int nbTachesCourantes = 3;
		mC.put(nbTachesCourantes, m3);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m3
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 3 taches");
		for(int i = 1; i < tache_number-1; i++) {
			for(int j = i+1; j < tache_number; j++ ) {
				for(int k = j+1; k < tache_number+1; k++) {
					String keys = TabToString(new int[]{i, j, k});
					System.out.printf("f({%d, %d, %d}) = [%d, %d, %d]\n", i, j, k, m3.get(keys)[0], m3.get(keys)[1], m3.get(keys)[2]);
				}
			}
		}
	}
	
	public synchronized static void calcM4() {
		/*
		 * Calcule m4
		 */
		HashMap<String, int[]> m3 = mC.get(3);
		HashMap<String, int[]> m4 = new HashMap<String, int[]>();

		for(int i = 1; i < tache_number-2; i++) {
		    for(int j = i+1; j < tache_number-1; j++ ) {
		        for(int k = j+1; k < tache_number; k++) {
		            for(int m = k+1; m < tache_number+1; m++) {
		                int x1 = 0, x2 = 0, x3 = 0, x4 = 0;
		                
		                String keys = TabToString(new int[]{i, j, k});
		                x1 = m3.get(keys)[2] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1] - pour_dueDate[m-1])));
		                keys = TabToString(new int[]{i, j, m});
		                x2 = m3.get(keys)[2] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1] - pour_dueDate[k-1])));
		                keys = TabToString(new int[]{i, k, m});
		                x3 = m3.get(keys)[2] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1] - pour_dueDate[j-1])));
		                keys = TabToString(new int[]{j, k, m});
		                x4 = m3.get(keys)[2] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1] - pour_dueDate[i-1])));
		                
		                
		                int[] values = new int[4];
		                if(x1 >= x4 && x2 >= x4 && x3 >= x4) {
		                    values = new int[]{j, k, m, x4};
		                } else if(x1 >= x3 && x2 >= x3 && x4 >= x3) {
		                    values = new int[]{i, k, m, x3};
		                } else if(x1 >= x2 && x3 >= x2 && x4 >= x2) {
		                    values = new int[]{i, j, m, x2};
		                } else if(x2 >= x1 && x3 >= x1 && x4 >= x1) {
		                    values = new int[]{i, j, k, x1};
		                }
		                
		                keys = TabToString(new int[]{i, j, k, m});
		                m4.put(keys, values);
		                
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m4 avec son indice
		 */
		int nbTachesCourantes = 4;
		mC.put(nbTachesCourantes, m4);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		* Affichage m4
		*/
		System.out.println();
		System.out.println();

		System.out.println("Valeurs pour 4 taches");
		for(int i = 1; i < tache_number-2; i++) {
		    for(int j = i+1; j < tache_number-1; j++ ) {
		        for(int k = j+1; k < tache_number; k++) {
		            for(int m = k+1; m < tache_number+1; m++) {
		                String keys = TabToString(new int[]{i, j, k, m});
		                System.out.printf("f({%d, %d, %d, %d}) = [%d, %d, %d, %d]\n", i, j, k, m, m4.get(keys)[0], m4.get(keys)[1], m4.get(keys)[2], m4.get(keys)[3]);
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM5() {
		/*
		 * Calcule m5
		 */
		HashMap<String, int[]> m4 = mC.get(4);
		HashMap<String, int[]> m5 = new HashMap<String, int[]>();

		for(int i = 1; i < tache_number-3; i++) {
		    for(int j = i+1; j < tache_number-2; j++ ) {
		        for(int k = j+1; k < tache_number-1; k++) {
		            for(int m = k+1; m < tache_number; m++) {
		                for(int n = m+1; n < tache_number+1; n++) {
	                        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;

	                        String keys = TabToString(new int[]{i, j, k, m});
	                        x1 = m4.get(keys)[3] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1] - pour_dueDate[n-1])));
	                        keys = TabToString(new int[]{i, j, k, n});
	                        x2 = m4.get(keys)[3] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1] - pour_dueDate[m-1])));
	                        keys = TabToString(new int[]{i, j, m, n});
	                        x3 = m4.get(keys)[3] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1] - pour_dueDate[k-1])));
	                        keys = TabToString(new int[]{i, k, m, n});
	                        x4 = m4.get(keys)[3] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1] - pour_dueDate[j-1])));
	                        keys = TabToString(new int[]{j, k, m, n});
	                        x5 = m4.get(keys)[3] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1] - pour_dueDate[i-1])));
	                        
	                        int[] values = new int[5];
	                        if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5) {
	                            values = new int[]{j, k, m, n, x5};
	                        }else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4) {
	                            values = new int[]{i, k, m, n, x4};
	                        } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3) {
	                            values = new int[]{i, j, m, n, x3};
	                        } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2) {
	                            values = new int[]{i, j, k, n, x2};
	                        } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1) {
	                            values = new int[]{i, j, k, m, x1};
	                        }

	                        keys = TabToString(new int[]{i, j, k, m, n});
	                        m5.put(keys, values);
	                    }
	                }
	            }
	        }
	    }

		/*
		 * Correspondre la m5 avec son indice
		 */
		int nbTachesCourantes = 5;
		mC.put(nbTachesCourantes, m5);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m5
		 */
		System.out.println();
		System.out.println();

		System.out.println("Valeurs pour 5 taches");
		for(int i = 1; i < tache_number-3; i++) {
		    for(int j = i+1; j < tache_number-2; j++ ) {
		        for(int k = j+1; k < tache_number-1; k++) {
		            for(int m = k+1; m < tache_number; m++) {
		                for(int n = m+1; n < tache_number+1; n++) {
		                    String keys = TabToString(new int[]{i, j, k, m, n});
		                    System.out.printf("f({%d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d]\n", i, j, k, m, n, m5.get(keys)[0], m5.get(keys)[1], m5.get(keys)[2], m5.get(keys)[3], m5.get(keys)[4]);
		                }
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM6() {
		/*
		 * Calcule m6
		 */
		HashMap<String, int[]> m5 = mC.get(5);
		HashMap<String, int[]> m6 = new HashMap<String, int[]>();

		for(int i = 1; i < tache_number-4; i++) {
		    for(int j = i+1; j < tache_number-3; j++ ) {
		        for(int k = j+1; k < tache_number-2; k++) {
		            for(int m = k+1; m < tache_number-1; m++) {
		                for(int n = m+1; n < tache_number; n++) {
		                    for(int o = n+1; o < tache_number+1; o++) {
		                        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0;
		                        String keys;
		                        
		                        keys = TabToString(new int[]{i, j, k, m, n});
		                        x1 = m5.get(keys)[4] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1] - pour_dueDate[o-1])));
		                        keys = TabToString(new int[]{i, j, k, m, o});
		                        x2 = m5.get(keys)[4] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1] - pour_dueDate[n-1])));
		                        keys = TabToString(new int[]{i, j, k, n, o});
		                        x3 = m5.get(keys)[4] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1] - pour_dueDate[m-1])));
		                        keys = TabToString(new int[]{i, j, m, n, o});
		                        x4 = m5.get(keys)[4] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1] - pour_dueDate[k-1])));
		                        keys = TabToString(new int[]{i, k, m, n, o});
		                        x5 = m5.get(keys)[4] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1] - pour_dueDate[j-1])));
		                        keys = TabToString(new int[]{j, k, m, n, o});
		                        x6 = m5.get(keys)[4] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1] - pour_dueDate[i-1])));
		                        
		                        int[] values = new int[6];
		                        if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6) {
		                            values = new int[]{j, k, m, n, o, x6};
		                        }else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5) {
		                            values = new int[]{i, k, m, n, o, x5};
		                        } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4) {
		                            values = new int[]{i, j, m, n, o, x4};
		                        } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3) {
		                            values = new int[]{i, j, k, n, o, x3};
		                        } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2) {
		                            values = new int[]{i, j, k, m, o, x2};
		                        } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1) {
		                            values = new int[]{i, j, k, m, n, x1};
		                        }

		                        keys = TabToString(new int[]{i, j, k, m, n, o});
		                        m6.put(keys, values);
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m2 avec son indice
		 */
		int nbTachesCourantes = 6;
		mC.put(nbTachesCourantes, m6);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m6
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 6 taches");
		for(int i = 1; i < tache_number-4; i++) {
		    for(int j = i+1; j < tache_number-3; j++ ) {
		        for(int k = j+1; k < tache_number-2; k++) {
		            for(int m = k+1; m < tache_number-1; m++) {
		                for(int n = m+1; n < tache_number; n++) {
		                    for(int o = n+1; o < tache_number+1; o++) {
		                        String keys = TabToString(new int[]{i, j, k, m, n, o});
		                        System.out.printf("f({%d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, m6.get(keys)[0], m6.get(keys)[1], m6.get(keys)[2], m6.get(keys)[3], m6.get(keys)[4], m6.get(keys)[5]);
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM7() {
		/*
		 * Calcule m7
		 */
		HashMap<String, int[]> m6 = mC.get(6);
		HashMap<String, int[]> m7 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-5; i++) {
		    for(int j = i+1; j < tache_number-4; j++ ) {
		        for(int k = j+1; k < tache_number-3; k++) {
		            for(int m = k+1; m < tache_number-2; m++) {
		                for(int n = m+1; n < tache_number-1; n++) {
		                    for(int o = n+1; o < tache_number; o++) {
		                        for(int p = o+1; p < tache_number+1; p++) {
		                            int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0;
		                            
		                            String keys = TabToString(new int[]{i, j, k, m, n, o});
		                            x1 = m6.get(keys)[5] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[p-1])));
		                            keys = TabToString(new int[]{i, j, k, m, n, p});
		                            x2 = m6.get(keys)[5] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[o-1])));
		                            keys = TabToString(new int[]{i, j, k, m, o, p});
		                            x3 = m6.get(keys)[5] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[n-1])));
		                            keys = TabToString(new int[]{i, j, k, n, o, p});
		                            x4 = m6.get(keys)[5] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[m-1])));
		                            keys = TabToString(new int[]{i, j, m, n, o, p});
		                            x5 = m6.get(keys)[5] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[k-1])));
		                            keys = TabToString(new int[]{i, k, m, n, o, p});
		                            x6 = m6.get(keys)[5] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[j-1])));
		                            keys = TabToString(new int[]{j, k, m, n, o, p});
		                            x7 = m6.get(keys)[5] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1] - pour_dueDate[i-1])));
		                            
		                            
		                            int[] values = new int[7];
		                            if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7) {
		                                values = new int[]{j, k, m, n, o, p, x7};
		                            }else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6) {
		                                values = new int[]{i, k, m, n, o, p, x6};
		                            } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5) {
		                                values = new int[]{i, j, m, n, o, p, x5};
		                            } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4) {
		                                values = new int[]{i, j, k, n, o, p, x4};
		                            } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3) {
		                                values = new int[]{i, j, k, m, o, p, x3};
		                            } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2) {
		                                values = new int[]{i, j, k, m, n, p, x2};
		                            } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1) {
		                                values = new int[]{i, j, k, m, n, o, x1};
		                            }
		                            

		                            keys = TabToString(new int[]{i, j, k, m, n, o, p});
		                            m7.put(keys, values);
		                            
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m7 avec son indice
		 */
		int nbTachesCourantes = 7;
		mC.put(nbTachesCourantes, m7);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m7
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 7 taches");
		for(int i = 1; i < tache_number-5; i++) {
		    for(int j = i+1; j < tache_number-4; j++ ) {
		        for(int k = j+1; k < tache_number-3; k++) {
		            for(int m = k+1; m < tache_number-2; m++) {
		                for(int n = m+1; n < tache_number-1; n++) {
		                    for(int o = n+1; o < tache_number; o++) {
		                        for(int p = o+1; p < tache_number+1; p++) {
		                            String keys = TabToString(new int[]{i, j, k, m, n, o, p});
		                            System.out.printf("f({%d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, m7.get(keys)[0], m7.get(keys)[1], m7.get(keys)[2], m7.get(keys)[3], m7.get(keys)[4], m7.get(keys)[5], m7.get(keys)[6]);
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

	}
	
	public synchronized static void calcM8() {
		/*
		 * Calcule m8
		 */
		HashMap<String, int[]> m7 = mC.get(7);
		HashMap<String, int[]> m8 = new HashMap<String, int[]>();
		
		for(int i = 1; i < tache_number-6; i++) {
		    for(int j = i+1; j < tache_number-5; j++ ) {
		        for(int k = j+1; k < tache_number-4; k++) {
		            for(int m = k+1; m < tache_number-3; m++) {
		                for(int n = m+1; n < tache_number-2; n++) {
		                    for(int o = n+1; o < tache_number-1; o++) {
		                        for(int p = o+1; p < tache_number; p++) {
		                            for(int q = p+1; q < tache_number+1; q++) {
		                                int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0;
		                                String keys;
		                                
		                                keys = TabToString(new int[]{i, j, k, m, n, o, p});
		                                x1 = m7.get(keys)[6] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[q-1])));
		                                keys = TabToString(new int[]{i, j, k, m, n, o, q});
		                                x2 = m7.get(keys)[6] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[p-1])));
		                                keys = TabToString(new int[]{i, j, k, m, n, p, q});
		                                x3 = m7.get(keys)[6] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[o-1])));
		                                keys = TabToString(new int[]{i, j, k, m, o, p, q});
		                                x4 = m7.get(keys)[6] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[n-1])));
		                                keys = TabToString(new int[]{i, j, k, n, o, p, q});
		                                x5 = m7.get(keys)[6] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[m-1])));
		                                keys = TabToString(new int[]{i, j, m, n, o, p, q});
		                                x6 = m7.get(keys)[6] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[k-1])));
		                                keys = TabToString(new int[]{i, k, m, n, o, p, q});
		                                x7 = m7.get(keys)[6] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[j-1])));
		                                keys = TabToString(new int[]{j, k, m, n, o, p, q});
		                                x8 = m7.get(keys)[6] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1] - pour_dueDate[i-1])));
		                                
		                                
		                                int[] values = new int[8];
		                                if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8) {
		                                    values = new int[]{j, k, m, n, o, p, q, x8};
		                                } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7) {
		                                    values = new int[]{j, k, m, n, o, p, q, x7};
		                                } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6) {
		                                    values = new int[]{i, j, m, n, o, p, q, x6};
		                                } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5) {
		                                    values = new int[]{i, j, k, n, o, p, q, x5};
		                                } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4) {
		                                    values = new int[]{i, j, k, m, o, p, q, x4};
		                                } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3) {
		                                    values = new int[]{i, j, k, m, n, p, q, x3};
		                                } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2) {
		                                    values = new int[]{i, j, k, m, n, o, q, x2};
		                                } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1) {
		                                    values = new int[]{i, j, k, m, n, o, p, x1};
		                                }
		                                
		                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q});
		                                m8.put(keys, values);
		                                
		                                
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m8 avec son indice
		 */
		int nbTachesCourantes = 8;
		mC.put(nbTachesCourantes, m8);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		* Affichage m8
		*/
		System.out.println();
		System.out.println();

		System.out.println("Valeurs pour 8 taches");
		for(int i = 1; i < tache_number-6; i++) {
		    for(int j = i+1; j < tache_number-5; j++ ) {
		        for(int k = j+1; k < tache_number-4; k++) {
		            for(int m = k+1; m < tache_number-3; m++) {
		                for(int n = m+1; n < tache_number-2; n++) {
		                    for(int o = n+1; o < tache_number-1; o++) {
		                        for(int p = o+1; p < tache_number; p++) {
		                            for(int q = p+1; q < tache_number+1; q++) {
		                                String keys = TabToString(new int[]{i, j, k, m, n, o, p, q});
		                                System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, m8.get(keys)[0], m8.get(keys)[1], m8.get(keys)[2], m8.get(keys)[3], m8.get(keys)[4], m8.get(keys)[5], m8.get(keys)[6], m8.get(keys)[7]);
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

	}
	
	public synchronized static void calcM9() {
		/*
		 * Calcule m9
		 */
		HashMap<String, int[]> m8 = mC.get(8);
		HashMap<String, int[]> m9 = new HashMap<String, int[]>();
		
		for(int i = 1; i < tache_number-7; i++) {
		    for(int j = i+1; j < tache_number-6; j++ ) {
		        for(int k = j+1; k < tache_number-5; k++) {
		            for(int m = k+1; m < tache_number-4; m++) {
		                for(int n = m+1; n < tache_number-3; n++) {
		                    for(int o = n+1; o < tache_number-2; o++) {
		                        for(int p = o+1; p < tache_number-1; p++) {
		                            for(int q = p+1; q < tache_number; q++) {
		                                for(int r = q+1; r < tache_number+1; r++) {
		                                    int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0;
		
		                                    String keys = TabToString(new int[]{i, j, k, m, n, o, p, q});
		                                    x1 = m8.get(keys)[7] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[r-1])));
		                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r});
		                                    x2 = m8.get(keys)[7] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[q-1])));
		                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r});
		                                    x3 = m8.get(keys)[7] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[p-1])));
		                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r});
		                                    x4 = m8.get(keys)[7] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[o-1])));
		                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r});
		                                    x5 = m8.get(keys)[7] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[n-1])));
		                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r});
		                                    x6 = m8.get(keys)[7] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[m-1])));
		                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r});
		                                    x7 = m8.get(keys)[7] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[k-1])));
		                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r});
		                                    x8 = m8.get(keys)[7] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[j-1])));
		                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r});
		                                    x9 = m8.get(keys)[7] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1] - pour_dueDate[i-1])));
		                                    
		                                    int[] values = new int[9];
		                                    if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9) {
		                                        values = new int[]{j, k, m, n, o, p, q, r, x9};
		                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8) {
		                                        values = new int[]{i, k, m, n, o, p, q, r, x8};
		                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7) {
		                                        values = new int[]{i, j, m, n, o, p, q, r, x7};
		                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6) {
		                                        values = new int[]{i, j, k, n, o, p, q, r, x6};
		                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5) {
		                                        values = new int[]{i, j, k, m, o, p, q, r, x5};
		                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4) {
		                                        values = new int[]{i, j, k, m, n, p, q, r, x4};
		                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3) {
		                                        values = new int[]{i, j, k, m, n, o, q, r, x3};
		                                    } else if(x1 >= x2 && x9 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2) {
		                                        values = new int[]{i, j, k, m, n, o, p, r, x2};
		                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1) {
		                                        values = new int[]{i, j, k, m, n, o, p, q, x1};
		                                    }
		                                    
		                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r});
		                                    m9.put(keys, values);
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m9 avec son indice
		 */
		int nbTachesCourantes = 9;
		mC.put(nbTachesCourantes, m9);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m9
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 9 taches");
		for(int i = 1; i < tache_number-7; i++) {
		    for(int j = i+1; j < tache_number-6; j++ ) {
		        for(int k = j+1; k < tache_number-5; k++) {
		            for(int m = k+1; m < tache_number-4; m++) {
		                for(int n = m+1; n < tache_number-3; n++) {
		                    for(int o = n+1; o < tache_number-2; o++) {
		                        for(int p = o+1; p < tache_number-1; p++) {
		                            for(int q = p+1; q < tache_number; q++) {
		                                for(int r = q+1; r < tache_number+1; r++) {
		                                    String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r});
		                                    System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, m9.get(keys)[0], m9.get(keys)[1], m9.get(keys)[2], m9.get(keys)[3], m9.get(keys)[4], m9.get(keys)[5], m9.get(keys)[6], m9.get(keys)[7], m9.get(keys)[8]);
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM10() {
		/*
		 * Calcule m10
		 */
		HashMap<String, int[]> m9 = mC.get(9);
		HashMap<String, int[]> m10 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-8; i++) {
		    for(int j = i+1; j < tache_number-7; j++ ) {
		        for(int k = j+1; k < tache_number-6; k++) {
		            for(int m = k+1; m < tache_number-5; m++) {
		                for(int n = m+1; n < tache_number-4; n++) {
		                    for(int o = n+1; o < tache_number-3; o++) {
		                        for(int p = o+1; p < tache_number-2; p++) {
		                            for(int q = p+1; q < tache_number-1; q++) {
		                                for(int r = q+1; r < tache_number; r++) {
		                                    for(int s = r+1; s < tache_number+1; s++) {
		                                    
		                                        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0;
		                                        String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r});
		                                        
		                                        x1 = m9.get(keys)[8] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[s-1])));
		                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s});
		                                        x2 = m9.get(keys)[8] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[r-1])));
		                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s});
		                                        x3 = m9.get(keys)[8] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[q-1])));
		                                        keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s});
		                                        x4 = m9.get(keys)[5] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[p-1])));
		                                        keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s});
		                                        x5 = m9.get(keys)[8] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[o-1])));
		                                        keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s});
		                                        x6 = m9.get(keys)[8] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[n-1])));
		                                        keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s});
		                                        x7 = m9.get(keys)[8] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[m-1])));
		                                        keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s});
		                                        x8 = m9.get(keys)[8] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[k-1])));
		                                        keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s});
		                                        x9 = m9.get(keys)[8] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[j-1])));
		                                        keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s});
		                                        x10 = m9.get(keys)[8] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1] - pour_dueDate[i-1])));
		                                        
		                                        
		                                        int[] values = new int[10];
		                                        if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10) {
		                                            values = new int[]{j, k, m, n, o, p, q, r, s, x10};
		                                        } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9) {
		                                            values = new int[]{i, k, m, n, o, p, q, r, s, x9};
		                                        } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8) {
		                                            values = new int[]{i, j, m, n, o, p, q, r, s, x8};
		                                        } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7) {
		                                            values = new int[]{i, j, k, n, o, p, q, r, s, x7};
		                                        } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6) {
		                                            values = new int[]{i, j, k, m, o, p, q, r, s, x6};
		                                        } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5) {
		                                            values = new int[]{i, j, k, m, n, p, q, r, s, x5};
		                                        } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4) {
		                                            values = new int[]{i, j, k, m, n, o, q, r, s, x4};
		                                        } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3) {
		                                            values = new int[]{i, j, k, m, n, o, p, r, s, x3};
		                                        } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2) {
		                                            values = new int[]{i, j, k, m, n, o, p, q, s, x2};
		                                        } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1) {
		                                            values = new int[]{i, j, k, m, n, o, p, q, r, x1};
		                                        }
		                                        
		                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s});
		                                        m10.put(keys, values);
		                                    
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m10 avec son indice
		 */
		int nbTachesCourantes = 10;
		mC.put(nbTachesCourantes, m10);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m10
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 10 taches");
		for(int i = 1; i < tache_number-8; i++) {
		    for(int j = i+1; j < tache_number-7; j++ ) {
		        for(int k = j+1; k < tache_number-6; k++) {
		            for(int m = k+1; m < tache_number-5; m++) {
		                for(int n = m+1; n < tache_number-4; n++) {
		                    for(int o = n+1; o < tache_number-3; o++) {
		                        for(int p = o+1; p < tache_number-2; p++) {
		                            for(int q = p+1; q < tache_number-1; q++) {
		                                for(int r = q+1; r < tache_number; r++) {
		                                    for(int s = r+1; s < tache_number+1; s++) {
		                                        String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s});
		                                        System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, m10.get(keys)[0], m10.get(keys)[1], m10.get(keys)[2], m10.get(keys)[3], m10.get(keys)[4], m10.get(keys)[5], m10.get(keys)[6], m10.get(keys)[7], m10.get(keys)[8], m10.get(keys)[9]);
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM11() {
		/*
		 * Calcule m11
		 */
		HashMap<String, int[]> m10 = mC.get(10);
		HashMap<String, int[]> m11 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-9; i++) {
		    for(int j = i+1; j < tache_number-8; j++ ) {
		        for(int k = j+1; k < tache_number-7; k++) {
		            for(int m = k+1; m < tache_number-6; m++) {
		                for(int n = m+1; n < tache_number-5; n++) {
		                    for(int o = n+1; o < tache_number-4; o++) {
		                        for(int p = o+1; p < tache_number-3; p++) {
		                            for(int q = p+1; q < tache_number-2; q++) {
		                                for(int r = q+1; r < tache_number-1; r++) {
		                                    for(int s = r+1; s < tache_number; s++) {
		                                        for(int t = s+1; t < tache_number+1; t++) {
		                                        
		                                            int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0;
		                                            String keys;
		                                            
		                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s});
		                                            x1 = m10.get(keys)[9] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[t-1])));
		                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t});
		                                            x2 = m10.get(keys)[9] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[s-1])));
		                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t});
		                                            x3 = m10.get(keys)[9] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[r-1])));
		                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t});
		                                            x4 = m10.get(keys)[9] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[q-1])));
		                                            keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t});
		                                            x5 = m10.get(keys)[9] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[p-1])));
		                                            keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t});
		                                            x6 = m10.get(keys)[9] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[o-1])));
		                                            keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t});
		                                            x7 = m10.get(keys)[9] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[n-1])));
		                                            keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t});
		                                            x8 = m10.get(keys)[9] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[m-1])));
		                                            keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t});
		                                            x9 = m10.get(keys)[9] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[k-1])));
		                                            keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t});
		                                            x10 = m10.get(keys)[9] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[j-1])));
		                                            keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t});
		                                            x11 = m10.get(keys)[9] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1] - pour_dueDate[i-1])));
		                                            
		                                            
		                                            int[] values = new int[11];
		                                            if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11) {
		                                                values = new int[]{j, k, m, n, o, p, q, r, s, t, x11};
		                                            } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10) {
		                                                values = new int[]{i, k, m, n, o, p, q, r, s, t, x10};
		                                            } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9) {
		                                                values = new int[]{i, j, m, n, o, p, q, r, s, t, x9};
		                                            } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8) {
		                                                values = new int[]{i, j, k, n, o, p, q, r, s, t, x8};
		                                            } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7) {
		                                                values = new int[]{i, j, k, m, o, p, q, r, s, t, x7};
		                                            } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6) {
		                                                values = new int[]{i, j, k, m, n, p, q, r, s, t, x6};
		                                            } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5) {
		                                                values = new int[]{i, j, k, m, n, o, q, r, s, t, x5};
		                                            } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4) {
		                                                values = new int[]{i, j, k, m, n, o, p, r, s, t, x4};
		                                            } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3) {
		                                                values = new int[]{i, j, k, m, n, o, p, q, s, t, x3};
		                                            } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2) {
		                                                values = new int[]{i, j, k, m, n, o, p, q, r, t, x2};
		                                            } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1) {
		                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, x1};
		                                            }
		                                            
		                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t});
		                                            m11.put(keys, values);
		                                            
		                                        }
		                                    
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m11 avec son indice
		 */
		int nbTachesCourantes = 11;
		mC.put(nbTachesCourantes, m11);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m11
		 */
		System.out.println();
		System.out.println();

		System.out.println("Valeurs pour 11 taches");
		for(int i = 1; i < tache_number-9; i++) {
		    for(int j = i+1; j < tache_number-8; j++ ) {
		        for(int k = j+1; k < tache_number-7; k++) {
		            for(int m = k+1; m < tache_number-6; m++) {
		                for(int n = m+1; n < tache_number-5; n++) {
		                    for(int o = n+1; o < tache_number-4; o++) {
		                        for(int p = o+1; p < tache_number-3; p++) {
		                            for(int q = p+1; q < tache_number-2; q++) {
		                                for(int r = q+1; r < tache_number-1; r++) {
		                                    for(int s = r+1; s < tache_number; s++) {
		                                        for(int t = s+1; t < tache_number+1; t++) {
		                                            String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t});
		                                            System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, m11.get(keys)[0], m11.get(keys)[1], m11.get(keys)[2], m11.get(keys)[3], m11.get(keys)[4], m11.get(keys)[5], m11.get(keys)[6], m11.get(keys)[7], m11.get(keys)[8], m11.get(keys)[9], m11.get(keys)[10]);
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM12() {
		/*
		 * Calcule m12
		 */
		HashMap<String, int[]> m11 = mC.get(11);
		HashMap<String, int[]> m12 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-10; i++) {
		    for(int j = i+1; j < tache_number-9; j++ ) {
		        for(int k = j+1; k < tache_number-8; k++) {
		            for(int m = k+1; m < tache_number-7; m++) {
		                for(int n = m+1; n < tache_number-6; n++) {
		                    for(int o = n+1; o < tache_number-5; o++) {
		                        for(int p = o+1; p < tache_number-4; p++) {
		                            for(int q = p+1; q < tache_number-3; q++) {
		                                for(int r = q+1; r < tache_number-2; r++) {
		                                    for(int s = r+1; s < tache_number-1; s++) {
		                                        for(int t = s+1; t < tache_number; t++) {
		                                            for(int u = t+1; u < tache_number+1; u++) {
		                                        
		                                                int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0;
		                                                String keys;
		                                                
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t});
		                                                x1 = m11.get(keys)[10] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[u-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u});
		                                                x2 = m11.get(keys)[10] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[t-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u});
		                                                x3 = m11.get(keys)[10] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[s-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u});
		                                                x4 = m11.get(keys)[10] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[r-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u});
		                                                x5 = m11.get(keys)[10] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[q-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u});
		                                                x6 = m11.get(keys)[10] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[p-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u});
		                                                x7 = m11.get(keys)[10] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[o-1])));
		                                                keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u});
		                                                x8 = m11.get(keys)[10] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[n-1])));
		                                                keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u});
		                                                x9 = m11.get(keys)[10] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[m-1])));
		                                                keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u});
		                                                x10 = m11.get(keys)[10] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[k-1])));
		                                                keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u});
		                                                x11 = m11.get(keys)[10] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[j-1])));
		                                                keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u});
		                                                x12 = m11.get(keys)[10] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1] - pour_dueDate[i-1])));
		                                                
		                                                
		                                                
		                                                int[] values = new int[12];
		                                                if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12) {
		                                                    values = new int[]{j, k, m, n, o, p, q, r, s, t, u, x12};
		                                                } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11) {
		                                                    values = new int[]{i, k, m, n, o, p, q, r, s, t, u, x11};
		                                                } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10) {
		                                                    values = new int[]{i, j, m, n, o, p, q, r, s, t, u, x10};
		                                                } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9) {
		                                                    values = new int[]{i, j, k, n, o, p, q, r, s, t, u, x9};
		                                                } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8) {
		                                                    values = new int[]{i, j, k, m, o, p, q, r, s, t, u, x8};
		                                                } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7) {
		                                                    values = new int[]{i, j, k, m, n, p, q, r, s, t, u, x7};
		                                                } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6) {
		                                                    values = new int[]{i, j, k, m, n, o, q, r, s, t, u, x6};
		                                                } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5) {
		                                                    values = new int[]{i, j, k, m, n, o, p, r, s, t, u, x5};
		                                                } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4) {
		                                                    values = new int[]{i, j, k, m, n, o, p, q, s, t, u, x4};
		                                                } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3) {
		                                                    values = new int[]{i, j, k, m, n, o, p, q, r, t, u, x3};
		                                                } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2) {
		                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, u, x2};
		                                                } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1) {
		                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, t, x1};
		                                                }
		                                                
		                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u});
		                                                m12.put(keys, values);
		                                            
		                                            }
		                                            
		                                        }
		                                    
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m12 avec son indice
		 */
		int nbTachesCourantes = 12;
		mC.put(nbTachesCourantes, m12);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m12
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 12 taches");
		for(int i = 1; i < tache_number-10; i++) {
		    for(int j = i+1; j < tache_number-9; j++ ) {
		        for(int k = j+1; k < tache_number-8; k++) {
		            for(int m = k+1; m < tache_number-7; m++) {
		                for(int n = m+1; n < tache_number-6; n++) {
		                    for(int o = n+1; o < tache_number-5; o++) {
		                        for(int p = o+1; p < tache_number-4; p++) {
		                            for(int q = p+1; q < tache_number-3; q++) {
		                                for(int r = q+1; r < tache_number-2; r++) {
		                                    for(int s = r+1; s < tache_number-1; s++) {
		                                        for(int t = s+1; t < tache_number; t++) {
		                                            for(int u = t+1; u < tache_number+1; u++) {
		                                                String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u});
		                                                System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, m12.get(keys)[0], m12.get(keys)[1], m12.get(keys)[2], m12.get(keys)[3], m12.get(keys)[4], m12.get(keys)[5], m12.get(keys)[6], m12.get(keys)[7], m12.get(keys)[8], m12.get(keys)[9], m12.get(keys)[10], m12.get(keys)[11]);
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM13() {
		/*
		 * Calcule m13
		 */
		HashMap<String, int[]> m12 = mC.get(12);
		HashMap<String, int[]> m13 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-11; i++) {
		    for(int j = i+1; j < tache_number-10; j++ ) {
		        for(int k = j+1; k < tache_number-9; k++) {
		            for(int m = k+1; m < tache_number-8; m++) {
		                for(int n = m+1; n < tache_number-7; n++) {
		                    for(int o = n+1; o < tache_number-6; o++) {
		                        for(int p = o+1; p < tache_number-5; p++) {
		                            for(int q = p+1; q < tache_number-4; q++) {
		                                for(int r = q+1; r < tache_number-3; r++) {
		                                    for(int s = r+1; s < tache_number-2; s++) {
		                                        for(int t = s+1; t < tache_number-1; t++) {
		                                            for(int u = t+1; u < tache_number; u++) {
		                                                for(int v = u+1; v < tache_number+1; v++) {
		                                        
		                                                    int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0, x13=0;
		                                                    String keys;
		                                                    
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u});
		                                                    x1 = m12.get(keys)[11] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[v-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v});
		                                                    x2 = m12.get(keys)[11] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[u-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v});
		                                                    x3 = m12.get(keys)[11] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[t-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v});
		                                                    x4 = m12.get(keys)[11] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[s-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v});
		                                                    x5 = m12.get(keys)[11] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[r-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v});
		                                                    x6 = m12.get(keys)[11] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[q-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v});
		                                                    x7 = m12.get(keys)[11] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[p-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v});
		                                                    x8 = m12.get(keys)[11] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[o-1])));
		                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v});
		                                                    x9 = m12.get(keys)[11] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[n-1])));
		                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v});
		                                                    x10 = m12.get(keys)[11] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[m-1])));
		                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v});
		                                                    x11 = m12.get(keys)[11] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[k-1])));
		                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v});
		                                                    x12 = m12.get(keys)[11] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[j-1])));
		                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v});
		                                                    x13 = m12.get(keys)[11] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1] - pour_dueDate[i-1])));
		                                                    
		                                                    
		                                                    int[] values = new int[13];
		                                                    if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13) {
		                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, x13};
		                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12) {
		                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, x12};
		                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11) {
		                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, x11};
		                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10) {
		                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, x10};
		                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9) {
		                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, x9};
		                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8) {
		                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, x8};
		                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7) {
		                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, x7};
		                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6) {
		                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, x6};
		                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5) {
		                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, x5};
		                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4) {
		                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, x4};
		                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3) {
		                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, x3};
		                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2) {
		                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, x2};
		                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1) {
		                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, x1};
		                                                    }
		                                                    
		                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v});
		                                                    m13.put(keys, values);
		                                                
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m13 avec son indice
		 */
		int nbTachesCourantes = 13;
		mC.put(nbTachesCourantes, m13);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m13
		 */
		System.out.println();
		System.out.println();

		System.out.println("Valeurs pour 13 taches");
		for(int i = 1; i < tache_number-11; i++) {
		    for(int j = i+1; j < tache_number-10; j++ ) {
		        for(int k = j+1; k < tache_number-9; k++) {
		            for(int m = k+1; m < tache_number-8; m++) {
		                for(int n = m+1; n < tache_number-7; n++) {
		                    for(int o = n+1; o < tache_number-6; o++) {
		                        for(int p = o+1; p < tache_number-5; p++) {
		                            for(int q = p+1; q < tache_number-4; q++) {
		                                for(int r = q+1; r < tache_number-3; r++) {
		                                    for(int s = r+1; s < tache_number-2; s++) {
		                                        for(int t = s+1; t < tache_number-1; t++) {
		                                            for(int u = t+1; u < tache_number; u++) {
		                                                for(int v = u+1; v < tache_number+1; v++) {
		                                                    String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v});
		                                                    System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, m13.get(keys)[0], m13.get(keys)[1], m13.get(keys)[2], m13.get(keys)[3], m13.get(keys)[4], m13.get(keys)[5], m13.get(keys)[6], m13.get(keys)[7], m13.get(keys)[8], m13.get(keys)[9], m13.get(keys)[10], m13.get(keys)[11], m13.get(keys)[12]);
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM14() {
		/*
		 * Calcule m14
		 */
		HashMap<String, int[]> m13 = mC.get(13);
		HashMap<String, int[]> m14 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-12; i++) {
		    for(int j = i+1; j < tache_number-11; j++ ) {
		        for(int k = j+1; k < tache_number-10; k++) {
		            for(int m = k+1; m < tache_number-9; m++) {
		                for(int n = m+1; n < tache_number-8; n++) {
		                    for(int o = n+1; o < tache_number-7; o++) {
		                        for(int p = o+1; p < tache_number-6; p++) {
		                            for(int q = p+1; q < tache_number-5; q++) {
		                                for(int r = q+1; r < tache_number-4; r++) {
		                                    for(int s = r+1; s < tache_number-3; s++) {
		                                        for(int t = s+1; t < tache_number-2; t++) {
		                                            for(int u = t+1; u < tache_number-1; u++) {
		                                                for(int v = u+1; v < tache_number; v++) {
		                                                    for(int w = v+1; w < tache_number+1; w++) {
		                                            
		                                                        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0, x13=0, x14=0;
		                                                        String keys;
		                                                        
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v});
		                                                        x1 = m13.get(keys)[12] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[w-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w});
		                                                        x2 = m13.get(keys)[12] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[v-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w});
		                                                        x3 = m13.get(keys)[12] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[u-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w});
		                                                        x4 = m13.get(keys)[12] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[t-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w});
		                                                        x5 = m13.get(keys)[12] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[s-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w});
		                                                        x6 = m13.get(keys)[12] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[r-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w});
		                                                        x7 = m13.get(keys)[12] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[q-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w});
		                                                        x8 = m13.get(keys)[12] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[p-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w});
		                                                        x9 = m13.get(keys)[12] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[o-1])));
		                                                        keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w});
		                                                        x10 = m13.get(keys)[12] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[n-1])));
		                                                        keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w});
		                                                        x11 = m13.get(keys)[12] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[m-1])));
		                                                        keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w});
		                                                        x12 = m13.get(keys)[12] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[k-1])));
		                                                        keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w});
		                                                        x13 = m13.get(keys)[12] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[j-1])));
		                                                        keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w});
		                                                        x14 = m13.get(keys)[12] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1] - pour_dueDate[i-1])));
		                                                                                                                    
		                                                        
		                                                        int[] values = new int[14];
		                                                        if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14) {
		                                                            values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x14};
		                                                        } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13) {
		                                                            values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x13};
		                                                        } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12) {
		                                                            values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x12};
		                                                        } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11) {
		                                                            values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x11};
		                                                        } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10) {
		                                                            values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x10};
		                                                        } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9) {
		                                                            values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x9};
		                                                        } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8) {
		                                                            values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x8};
		                                                        } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7) {
		                                                            values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x7};
		                                                        } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6) {
		                                                            values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x6};
		                                                        } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5) {
		                                                            values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x5};
		                                                        } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4) {
		                                                            values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x4};
		                                                        } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3) {
		                                                            values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x3};
		                                                        } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2) {
		                                                            values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x2};
		                                                        } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1) {
		                                                            values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x1};
		                                                        }
		                                                        
		                                                        
		                                                        keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w});
		                                                        m14.put(keys, values);
		                                                        
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m14 avec son indice
		 */
		int nbTachesCourantes = 14;
		mC.put(nbTachesCourantes, m14);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m14
		 */
		System.out.println();
		System.out.println();

		System.out.println("Valeurs pour 14 taches");
		for(int i = 1; i < tache_number-12; i++) {
		    for(int j = i+1; j < tache_number-11; j++ ) {
		        for(int k = j+1; k < tache_number-10; k++) {
		            for(int m = k+1; m < tache_number-9; m++) {
		                for(int n = m+1; n < tache_number-8; n++) {
		                    for(int o = n+1; o < tache_number-7; o++) {
		                        for(int p = o+1; p < tache_number-6; p++) {
		                            for(int q = p+1; q < tache_number-5; q++) {
		                                for(int r = q+1; r < tache_number-4; r++) {
		                                    for(int s = r+1; s < tache_number-3; s++) {
		                                        for(int t = s+1; t < tache_number-2; t++) {
		                                            for(int u = t+1; u < tache_number-1; u++) {
		                                                for(int v = u+1; v < tache_number; v++) {
		                                                    for(int w = v+1; w < tache_number+1; w++) {
		                                                        String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w});
		                                                        System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, m14.get(keys)[0], m14.get(keys)[1], m14.get(keys)[2], m14.get(keys)[3], m14.get(keys)[4], m14.get(keys)[5], m14.get(keys)[6], m14.get(keys)[7], m14.get(keys)[8], m14.get(keys)[9], m14.get(keys)[10], m14.get(keys)[11], m14.get(keys)[12], m14.get(keys)[13]);
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM15() {
		/*
		 * Calcule m15
		 */
		HashMap<String, int[]> m14 = mC.get(14);
		HashMap<String, int[]> m15 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-13; i++) {
		    for(int j = i+1; j < tache_number-12; j++ ) {
		        for(int k = j+1; k < tache_number-11; k++) {
		            for(int m = k+1; m < tache_number-10; m++) {
		                for(int n = m+1; n < tache_number-9; n++) {
		                    for(int o = n+1; o < tache_number-8; o++) {
		                        for(int p = o+1; p < tache_number-7; p++) {
		                            for(int q = p+1; q < tache_number-6; q++) {
		                                for(int r = q+1; r < tache_number-5; r++) {
		                                    for(int s = r+1; s < tache_number-4; s++) {
		                                        for(int t = s+1; t < tache_number-3; t++) {
		                                            for(int u = t+1; u < tache_number-2; u++) {
		                                                for(int v = u+1; v < tache_number-1; v++) {
		                                                    for(int w = v+1; w < tache_number; w++) {
		                                                        for(int x = w+1; x < tache_number+1; x++) {
		                                                
		                                                            int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0, x13=0, x14=0, x15=0;
		                                                            String keys;
		                                                            
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w});
		                                                            x1 = m14.get(keys)[13] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[x-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x});
		                                                            x2 = m14.get(keys)[13] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[w-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x});
		                                                            x3 = m14.get(keys)[13] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[v-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x});
		                                                            x4 = m14.get(keys)[13] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[u-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x});
		                                                            x5 = m14.get(keys)[13] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[t-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x});
		                                                            x6 = m14.get(keys)[13] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[s-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x});
		                                                            x7 = m14.get(keys)[13] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[r-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x});
		                                                            x8 = m14.get(keys)[13] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[q-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x});
		                                                            x9 = m14.get(keys)[13] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[p-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x});
		                                                            x10 = m14.get(keys)[13] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[o-1])));
		                                                            keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x});
		                                                            x11 = m14.get(keys)[13] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[n-1])));
		                                                            keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x});
		                                                            x12 = m14.get(keys)[13] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[m-1])));
		                                                            keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x});
		                                                            x13 = m14.get(keys)[13] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[q-1])));
		                                                            keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x});
		                                                            x14 = m14.get(keys)[13] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[j-1])));
		                                                            keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x});
		                                                            x15 = m14.get(keys)[13] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1] - pour_dueDate[i-1])));
		                                                        
		                                                            
		                                                            int[] values = new int[15];
		                                                            if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15) {
		                                                                values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, x15};
		                                                            } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14) {
		                                                                values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, x14};
		                                                            } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13) {
		                                                                values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, x13};
		                                                            } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12) {
		                                                                values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, x12};
		                                                            } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11) {
		                                                                values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, x11};
		                                                            } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10) {
		                                                                values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, x10};
		                                                            } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9) {
		                                                                values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, x9};
		                                                            } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, x8};
		                                                            } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, x7};
		                                                            } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, x6};
		                                                            } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, x5};
		                                                            } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, x4};
		                                                            } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, x3};
		                                                            } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, x2};
		                                                            } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1) {
		                                                                values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x1};
		                                                            }
		                                                            
		                                                            
		                                                            keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x});
		                                                            m15.put(keys, values);
		                                                            
		                                                        }
		                                                        
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m15 avec son indice
		 */
		int nbTachesCourantes = 15;
		mC.put(nbTachesCourantes, m15);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m15
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 15 taches");
		for(int i = 1; i < tache_number-13; i++) {
		    for(int j = i+1; j < tache_number-12; j++ ) {
		        for(int k = j+1; k < tache_number-11; k++) {
		            for(int m = k+1; m < tache_number-10; m++) {
		                for(int n = m+1; n < tache_number-9; n++) {
		                    for(int o = n+1; o < tache_number-8; o++) {
		                        for(int p = o+1; p < tache_number-7; p++) {
		                            for(int q = p+1; q < tache_number-6; q++) {
		                                for(int r = q+1; r < tache_number-5; r++) {
		                                    for(int s = r+1; s < tache_number-4; s++) {
		                                        for(int t = s+1; t < tache_number-3; t++) {
		                                            for(int u = t+1; u < tache_number-2; u++) {
		                                                for(int v = u+1; v < tache_number-1; v++) {
		                                                    for(int w = v+1; w < tache_number; w++) {
		                                                        for(int x = w+1; x < tache_number+1; x++) {
		                                                            String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x});
		                                                            System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, m15.get(keys)[0], m15.get(keys)[1], m15.get(keys)[2], m15.get(keys)[3], m15.get(keys)[4], m15.get(keys)[5], m15.get(keys)[6], m15.get(keys)[7], m15.get(keys)[8], m15.get(keys)[9], m15.get(keys)[10], m15.get(keys)[11], m15.get(keys)[12], m15.get(keys)[13], m15.get(keys)[14]);
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM16() {
		/*
		 * Calcule m16
		 */
		HashMap<String, int[]> m15 = mC.get(15);
		HashMap<String, int[]> m16 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-14; i++) {
		    for(int j = i+1; j < tache_number-13; j++ ) {
		        for(int k = j+1; k < tache_number-12; k++) {
		            for(int m = k+1; m < tache_number-11; m++) {
		                for(int n = m+1; n < tache_number-10; n++) {
		                    for(int o = n+1; o < tache_number-9; o++) {
		                        for(int p = o+1; p < tache_number-8; p++) {
		                            for(int q = p+1; q < tache_number-7; q++) {
		                                for(int r = q+1; r < tache_number-6; r++) {
		                                    for(int s = r+1; s < tache_number-5; s++) {
		                                        for(int t = s+1; t < tache_number-4; t++) {
		                                            for(int u = t+1; u < tache_number-3; u++) {
		                                                for(int v = u+1; v < tache_number-2; v++) {
		                                                    for(int w = v+1; w < tache_number-1; w++) {
		                                                        for(int x = w+1; x < tache_number; x++) {
		                                                            for(int y = x+1; y < tache_number+1; y++) {
		                                                
		                                                                int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0;
		                                                                String keys;
		                                                                
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x});
		                                                                x1 = m15.get(keys)[14] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[y-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y});
		                                                                x2 = m15.get(keys)[14] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[x-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y});
		                                                                x3 = m15.get(keys)[14] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[w-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y});
		                                                                x4 = m15.get(keys)[14] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[v-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y});
		                                                                x5 = m15.get(keys)[14] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[u-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y});
		                                                                x6 = m15.get(keys)[14] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[t-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y});
		                                                                x7 = m15.get(keys)[14] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[s-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y});
		                                                                x8 = m15.get(keys)[14] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[r-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y});
		                                                                x9 = m15.get(keys)[14] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[q-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y});
		                                                                x10 = m15.get(keys)[14] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[p-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y});
		                                                                x11 = m15.get(keys)[14] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[o-1])));
		                                                                keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y});
		                                                                x12 = m15.get(keys)[14] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[n-1])));
		                                                                keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                x13 = m15.get(keys)[14] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[m-1])));
		                                                                keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                x14 = m15.get(keys)[14] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[q-1])));
		                                                                keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                x15 = m15.get(keys)[14] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[j-1])));
		                                                                keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                x16 = m15.get(keys)[14] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1] - pour_dueDate[i-1])));

		                                                                
		                                                                int[] values = new int[16];
		                                                                if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16) {
		                                                                    values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, x16};
		                                                                } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15) {
		                                                                    values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, x15};
		                                                                } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14) {
		                                                                    values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, x14};
		                                                                } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13) {
		                                                                    values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, x13};
		                                                                } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12) {
		                                                                    values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, x12};
		                                                                } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11) {
		                                                                    values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, x11};
		                                                                } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10) {
		                                                                    values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, x10};
		                                                                } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, x9};
		                                                                } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, x8};
		                                                                } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, x7};
		                                                                } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, x6};
		                                                                } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, x5};
		                                                                } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, x4};
		                                                                } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, x3};
		                                                                } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, x2};
		                                                                } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1) {
		                                                                    values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, x1};
		                                                                }

		                                                                keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                m16.put(keys, values);
		                                                            
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m16 avec son indice
		 */
		int nbTachesCourantes = 16;
		mC.put(nbTachesCourantes, m16);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Calcule m16
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 16 taches");
		for(int i = 1; i < tache_number-14; i++) {
		    for(int j = i+1; j < tache_number-13; j++ ) {
		        for(int k = j+1; k < tache_number-12; k++) {
		            for(int m = k+1; m < tache_number-11; m++) {
		                for(int n = m+1; n < tache_number-10; n++) {
		                    for(int o = n+1; o < tache_number-9; o++) {
		                        for(int p = o+1; p < tache_number-8; p++) {
		                            for(int q = p+1; q < tache_number-7; q++) {
		                                for(int r = q+1; r < tache_number-6; r++) {
		                                    for(int s = r+1; s < tache_number-5; s++) {
		                                        for(int t = s+1; t < tache_number-4; t++) {
		                                            for(int u = t+1; u < tache_number-3; u++) {
		                                                for(int v = u+1; v < tache_number-2; v++) {
		                                                    for(int w = v+1; w < tache_number-1; w++) {
		                                                        for(int x = w+1; x < tache_number; x++) {
		                                                            for(int y = x+1; y < tache_number+1; y++) {
		                                                                String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, m16.get(keys)[0], m16.get(keys)[1], m16.get(keys)[2], m16.get(keys)[3], m16.get(keys)[4], m16.get(keys)[5], m16.get(keys)[6], m16.get(keys)[7], m16.get(keys)[8], m16.get(keys)[9], m16.get(keys)[10], m16.get(keys)[11], m16.get(keys)[12], m16.get(keys)[13], m16.get(keys)[14], m16.get(keys)[15]);
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM17() {
		/*
		 * Calcule m17
		 */
		HashMap<String, int[]> m16 = mC.get(16);
		HashMap<String, int[]> m17 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-15; i++) {
		    for(int j = i+1; j < tache_number-14; j++ ) {
		        for(int k = j+1; k < tache_number-13; k++) {
		            for(int m = k+1; m < tache_number-12; m++) {
		                for(int n = m+1; n < tache_number-11; n++) {
		                    for(int o = n+1; o < tache_number-10; o++) {
		                        for(int p = o+1; p < tache_number-9; p++) {
		                            for(int q = p+1; q < tache_number-8; q++) {
		                                for(int r = q+1; r < tache_number-7; r++) {
		                                    for(int s = r+1; s < tache_number-6; s++) {
		                                        for(int t = s+1; t < tache_number-5; t++) {
		                                            for(int u = t+1; u < tache_number-4; u++) {
		                                                for(int v = u+1; v < tache_number-3; v++) {
		                                                    for(int w = v+1; w < tache_number-2; w++) {
		                                                        for(int x = w+1; x < tache_number-1; x++) {
		                                                            for(int y = x+1; y < tache_number; y++) {
		                                                                for(int z = y+1; z < tache_number+1; z++) {
		                                                                    
		                                                                    int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0;
		                                                                    String keys;
		                                                                    
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y});
		                                                                    x1 = m16.get(keys)[15] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[z-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z});
		                                                                    x2 = m16.get(keys)[15] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[y-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z});
		                                                                    x3 = m16.get(keys)[15] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[x-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z});
		                                                                    x4 = m16.get(keys)[15] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[w-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z});
		                                                                    x5 = m16.get(keys)[15] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[v-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z});
		                                                                    x6 = m16.get(keys)[15] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[u-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z});
		                                                                    x7 = m16.get(keys)[15] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[t-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z});
		                                                                    x8 = m16.get(keys)[15] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[s-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z});
		                                                                    x9 = m16.get(keys)[15] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[r-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z});
		                                                                    x10 = m16.get(keys)[15] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[q-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z});
		                                                                    x11 = m16.get(keys)[15] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[p-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    x12 = m16.get(keys)[15] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[o-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    x13 = m16.get(keys)[15] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[n-1])));
		                                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    x14 = m16.get(keys)[15] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[m-1])));
		                                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    x15 = m16.get(keys)[15] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[k-1])));
		                                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    x16 = m16.get(keys)[15] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[j-1])));
		                                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    x17 = m16.get(keys)[15] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1] - pour_dueDate[i-1])));

		                                                                    int[] values = new int[17];
		                                                                    if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17) {
		                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, x17};
		                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16) {
		                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, x16};
		                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15) {
		                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, x15};
		                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14) {
		                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, x14};
		                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13) {
		                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, x13};
		                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12) {
		                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, x12};
		                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11) {
		                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, x11};
		                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, x10};
		                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, x9};
		                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, x8};
		                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, x7};
		                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, x6};
		                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, x5};
		                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, x4};
		                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, x3};
		                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, x2};
		                                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1) {
		                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, x1};
		                                                                    }
		                                                                    
		                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    m17.put(keys, values);
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m17 avec son indice
		 */
		int nbTachesCourantes = 17;
		mC.put(nbTachesCourantes, m17);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m17
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 17 taches");
		for(int i = 1; i < tache_number-15; i++) {
		    for(int j = i+1; j < tache_number-14; j++ ) {
		        for(int k = j+1; k < tache_number-13; k++) {
		            for(int m = k+1; m < tache_number-12; m++) {
		                for(int n = m+1; n < tache_number-11; n++) {
		                    for(int o = n+1; o < tache_number-10; o++) {
		                        for(int p = o+1; p < tache_number-9; p++) {
		                            for(int q = p+1; q < tache_number-8; q++) {
		                                for(int r = q+1; r < tache_number-7; r++) {
		                                    for(int s = r+1; s < tache_number-6; s++) {
		                                        for(int t = s+1; t < tache_number-5; t++) {
		                                            for(int u = t+1; u < tache_number-4; u++) {
		                                                for(int v = u+1; v < tache_number-3; v++) {
		                                                    for(int w = v+1; w < tache_number-2; w++) {
		                                                        for(int x = w+1; x < tache_number-1; x++) {
		                                                            for(int y = x+1; y < tache_number; y++) {
		                                                                for(int z = y+1; z < tache_number+1; z++) {
		                                                                    String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z});
		                                                                    System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, m17.get(keys)[0], m17.get(keys)[1], m17.get(keys)[2], m17.get(keys)[3], m17.get(keys)[4], m17.get(keys)[5], m17.get(keys)[6], m17.get(keys)[7], m17.get(keys)[8], m17.get(keys)[9], m17.get(keys)[10], m17.get(keys)[11], m17.get(keys)[12], m17.get(keys)[13], m17.get(keys)[14], m17.get(keys)[15], m17.get(keys)[16]);
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM18() {
		/*
		 * Calcule m18
		 */
		HashMap<String, int[]> m17 = mC.get(17);
		HashMap<String, int[]> m18 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-16; i++) {
		    for(int j = i+1; j < tache_number-15; j++ ) {
		        for(int k = j+1; k < tache_number-14; k++) {
		            for(int m = k+1; m < tache_number-13; m++) {
		                for(int n = m+1; n < tache_number-12; n++) {
		                    for(int o = n+1; o < tache_number-11; o++) {
		                        for(int p = o+1; p < tache_number-10; p++) {
		                            for(int q = p+1; q < tache_number-9; q++) {
		                                for(int r = q+1; r < tache_number-8; r++) {
		                                    for(int s = r+1; s < tache_number-7; s++) {
		                                        for(int t = s+1; t < tache_number-6; t++) {
		                                            for(int u = t+1; u < tache_number-5; u++) {
		                                                for(int v = u+1; v < tache_number-4; v++) {
		                                                    for(int w = v+1; w < tache_number-3; w++) {
		                                                        for(int x = w+1; x < tache_number-2; x++) {
		                                                            for(int y = x+1; y < tache_number-1; y++) {
		                                                                for(int z = y+1; z < tache_number; z++) {
			                                                                for(int I = z+1; I < tache_number+1; I++) {
			                                                                    
			                                                                    int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 =0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0, x18=0;
			                                                                    String keys;
	
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z});
																				x1 = m17.get(keys)[16] + (pour_weight[I-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[I-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I});
																				x2 = m17.get(keys)[16] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[z-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I});
																				x3 = m17.get(keys)[16] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[y-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I});
																				x4 = m17.get(keys)[16] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[x-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I});
																				x5 = m17.get(keys)[16] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[w-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I});
																				x6 = m17.get(keys)[16] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[v-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I});
																				x7 = m17.get(keys)[16] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[u-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I});
																				x8 = m17.get(keys)[16] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[t-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I});
																				x9 = m17.get(keys)[16] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[s-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I});
																				x10 = m17.get(keys)[16] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[r-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I});
																				x11 = m17.get(keys)[16] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[q-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I});
																				x12 = m17.get(keys)[16] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[p-1])));
																				keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I});
																				x13 = m17.get(keys)[16] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[o-1])));
																				keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I});
																				x14 = m17.get(keys)[16] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[n-1])));
																				keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
																				x15 = m17.get(keys)[16] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[m-1])));
																				keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
																				x16 = m17.get(keys)[16] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[k-1])));
																				keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
																				x17 = m17.get(keys)[16] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[j-1])));
																				keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
																				x18 = m17.get(keys)[16] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1] - pour_dueDate[i-1])));
																				
			                                                                    int[] values = new int[18];
			                                                                    if(x1 >= x18 && x2 >= x18 && x3 >= x18 && x4 >= x18 && x5 >= x18 && x6 >= x18 && x7 >= x18 && x8 >= x18 && x9 >= x18 && x10 >= x18 && x11 >= x18 && x12 >= x18 && x13 >= x18 && x14 >= x18 && x15 >= x18 && x16 >= x18 && x17 >= x18) {
			                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, x18};
			                                                                    } else if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17 && x18 >= x17) {
			                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, x17};
			                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16 && x18 >= x16) {
			                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, x16};
			                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15 && x18 >= x15) {
			                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, x15};
			                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14 && x18 >= x14) {
			                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, x14};
			                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13 && x18 >= x13) {
			                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, x13};
			                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12 && x18 >= x12) {
			                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, x12};
			                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11 && x18 >= x11) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, x11};
			                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10 && x18 >= x10) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, x10};
			                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9 && x18 >= x9) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, x9};
			                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8 && x18 >= x8) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, x8};
			                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7 && x18 >= x7) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, x7};
			                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6 && x18 >= x6) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, x6};
			                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5 && x18 >= x5) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, x5};
			                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4 && x18 >= x4) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, x4};
			                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3 && x18 >= x3) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, x3};
			                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2 && x18 >= x2) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, x2};
			                                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1 && x18 >= x1) {
			                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, x1};
			                                                                    }
			                                                                    
			                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
			                                                                    m18.put(keys, values);
			                                                                    
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m18 avec son indice
		 */
		int nbTachesCourantes = 18;
		mC.put(nbTachesCourantes, m18);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}
		
		/*
		 * Affichage m18
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 18 taches");
		for(int i = 1; i < tache_number-16; i++) {
		    for(int j = i+1; j < tache_number-15; j++ ) {
		        for(int k = j+1; k < tache_number-14; k++) {
		            for(int m = k+1; m < tache_number-13; m++) {
		                for(int n = m+1; n < tache_number-12; n++) {
		                    for(int o = n+1; o < tache_number-11; o++) {
		                        for(int p = o+1; p < tache_number-10; p++) {
		                            for(int q = p+1; q < tache_number-9; q++) {
		                                for(int r = q+1; r < tache_number-8; r++) {
		                                    for(int s = r+1; s < tache_number-7; s++) {
		                                        for(int t = s+1; t < tache_number-6; t++) {
		                                            for(int u = t+1; u < tache_number-5; u++) {
		                                                for(int v = u+1; v < tache_number-4; v++) {
		                                                    for(int w = v+1; w < tache_number-3; w++) {
		                                                        for(int x = w+1; x < tache_number-2; x++) {
		                                                            for(int y = x+1; y < tache_number-1; y++) {
		                                                                for(int z = y+1; z < tache_number; z++) {
			                                                                for(int I = z+1; I < tache_number+1; I++) {
			                                                                    String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
			                                                                    System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, m18.get(keys)[0], m18.get(keys)[1], m18.get(keys)[2], m18.get(keys)[3], m18.get(keys)[4], m18.get(keys)[5], m18.get(keys)[6], m18.get(keys)[7], m18.get(keys)[8], m18.get(keys)[9], m18.get(keys)[10], m18.get(keys)[11], m18.get(keys)[12], m18.get(keys)[13], m18.get(keys)[14], m18.get(keys)[15], m18.get(keys)[16], m18.get(keys)[17]);
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM19() {
		/*
		 * Calcule m19
		 */
		HashMap<String, int[]> m18 = mC.get(18);
		HashMap<String, int[]> m19 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-17; i++) {
		    for(int j = i+1; j < tache_number-16; j++ ) {
		        for(int k = j+1; k < tache_number-15; k++) {
		            for(int m = k+1; m < tache_number-14; m++) {
		                for(int n = m+1; n < tache_number-13; n++) {
		                    for(int o = n+1; o < tache_number-12; o++) {
		                        for(int p = o+1; p < tache_number-11; p++) {
		                            for(int q = p+1; q < tache_number-10; q++) {
		                                for(int r = q+1; r < tache_number-9; r++) {
		                                    for(int s = r+1; s < tache_number-8; s++) {
		                                        for(int t = s+1; t < tache_number-7; t++) {
		                                            for(int u = t+1; u < tache_number-6; u++) {
		                                                for(int v = u+1; v < tache_number-5; v++) {
		                                                    for(int w = v+1; w < tache_number-4; w++) {
		                                                        for(int x = w+1; x < tache_number-3; x++) {
		                                                            for(int y = x+1; y < tache_number-2; y++) {
		                                                                for(int z = y+1; z < tache_number-1; z++) {
			                                                                for(int I = z+1; I < tache_number; I++) {
				                                                                for(int J = I+1; J < tache_number+1; J++) {
			                                                                    
				                                                                    int x1=0, x2=0, x3=0, x4=0, x5=0, x6=0, x7=0, x8=0, x9=0, x10=0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0, x18=0, x19=0;
				                                                                    String keys;
		
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I});
				                                                                    x1 = m18.get(keys)[17] + (pour_weight[J-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[J-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J});
				                                                                    x2 = m18.get(keys)[17] + (pour_weight[I-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[I-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J});
				                                                                    x3 = m18.get(keys)[17] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[z-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J});
				                                                                    x4 = m18.get(keys)[17] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[y-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J});
				                                                                    x5 = m18.get(keys)[17] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[x-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J});
				                                                                    x6 = m18.get(keys)[17] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[w-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J});
				                                                                    x7 = m18.get(keys)[17] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[v-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J});
				                                                                    x8 = m18.get(keys)[17] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[u-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J});
				                                                                    x9 = m18.get(keys)[17] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[t-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J});
				                                                                    x10 = m18.get(keys)[17] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[s-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J});
				                                                                    x11 = m18.get(keys)[17] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[r-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x12 = m18.get(keys)[17] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[q-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x13 = m18.get(keys)[17] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[p-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x14 = m18.get(keys)[17] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[o-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x15 = m18.get(keys)[17] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[n-1])));
				                                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x16 = m18.get(keys)[17] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[m-1])));
				                                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x17 = m18.get(keys)[17] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[k-1])));
				                                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x18 = m18.get(keys)[17] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[j-1])));
				                                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    x19 = m18.get(keys)[17] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1] - pour_dueDate[i-1])));
				                                                                    
				                                                                    int[] values = new int[19];
				                                                                    if(x1 >= x19 && x2 >= x19 && x3 >= x19 && x4 >= x19 && x5 >= x19 && x6 >= x19 && x7 >= x19 && x8 >= x19 && x9 >= x19 && x10 >= x19 && x11 >= x19 && x12 >= x19 && x13 >= x19 && x14 >= x19 && x15 >= x19 && x16 >= x19 && x17 >= x19 && x18 >= x19)  {
				                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, x19};
				                                                                    } else if(x1 >= x18 && x2 >= x18 && x3 >= x18 && x4 >= x18 && x5 >= x18 && x6 >= x18 && x7 >= x18 && x8 >= x18 && x9 >= x18 && x10 >= x18 && x11 >= x18 && x12 >= x18 && x13 >= x18 && x14 >= x18 && x15 >= x18 && x16 >= x18 && x17 >= x18 && x19 >= x18)  {
				                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, x18};
				                                                                    } else if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17 && x18 >= x17 && x19 >= x17)  {
				                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, x17};
				                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16 && x18 >= x16 && x19 >= x16)  {
				                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, x16};
				                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15 && x18 >= x15 && x19 >= x15)  {
				                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, x15};
				                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14 && x18 >= x14 && x19 >= x14)  {
				                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, x14};
				                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13 && x18 >= x13 && x19 >= x13)  {
				                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, x13};
				                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12 && x18 >= x12 && x19 >= x12)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, x12};
				                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11 && x18 >= x11 && x19 >= x11)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, x11};
				                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10 && x18 >= x10 && x19 >= x10)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, x10};
				                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9 && x18 >= x9 && x19 >= x9)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, x9};
				                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8 && x18 >= x8 && x19 >= x8)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, x8};
				                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7 && x18 >= x7 && x19 >= x7)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, x7};
				                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6 && x18 >= x6 && x19 >= x6)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, x6};
				                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5 && x18 >= x5 && x19 >= x5)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, x5};
				                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4 && x18 >= x4 && x19 >= x4)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, x4};
				                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3 && x18 >= x3 && x19 >= x3)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, x3};
				                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2 && x18 >= x2 && x19 >= x2)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, x2};
				                                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1 && x18 >= x1 && x19 >= x1)  {
				                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, x1};
				                                                                    }
				                                                                    
				                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                    m19.put(keys, values);
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m19 avec son indice
		 */
		int nbTachesCourantes = 19;
		mC.put(nbTachesCourantes, m19);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m19
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 19 taches");
		for(int i = 1; i < tache_number-17; i++) {
		    for(int j = i+1; j < tache_number-16; j++ ) {
		        for(int k = j+1; k < tache_number-15; k++) {
		            for(int m = k+1; m < tache_number-14; m++) {
		                for(int n = m+1; n < tache_number-13; n++) {
		                    for(int o = n+1; o < tache_number-12; o++) {
		                        for(int p = o+1; p < tache_number-11; p++) {
		                            for(int q = p+1; q < tache_number-10; q++) {
		                                for(int r = q+1; r < tache_number-9; r++) {
		                                    for(int s = r+1; s < tache_number-8; s++) {
		                                        for(int t = s+1; t < tache_number-7; t++) {
		                                            for(int u = t+1; u < tache_number-6; u++) {
		                                                for(int v = u+1; v < tache_number-5; v++) {
		                                                    for(int w = v+1; w < tache_number-4; w++) {
		                                                        for(int x = w+1; x < tache_number-3; x++) {
		                                                            for(int y = x+1; y < tache_number-2; y++) {
		                                                                for(int z = y+1; z < tache_number-1; z++) {
			                                                                for(int I = z+1; I < tache_number; I++) {
				                                                                for(int J = I+1; J < tache_number+1; J++) {
				                                                                	String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
				                                                                	System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, m19.get(keys)[0], m19.get(keys)[1], m19.get(keys)[2], m19.get(keys)[3], m19.get(keys)[4], m19.get(keys)[5], m19.get(keys)[6], m19.get(keys)[7], m19.get(keys)[8], m19.get(keys)[9], m19.get(keys)[10], m19.get(keys)[11], m19.get(keys)[12], m19.get(keys)[13], m19.get(keys)[14], m19.get(keys)[15], m19.get(keys)[16], m19.get(keys)[17], m19.get(keys)[18]);
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM20() {
		/*
		 * Calcule m20
		 */
		HashMap<String, int[]> m19 = mC.get(19);
		HashMap<String, int[]> m20 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-18; i++) {
		    for(int j = i+1; j < tache_number-17; j++ ) {
		        for(int k = j+1; k < tache_number-16; k++) {
		            for(int m = k+1; m < tache_number-15; m++) {
		                for(int n = m+1; n < tache_number-14; n++) {
		                    for(int o = n+1; o < tache_number-13; o++) {
		                        for(int p = o+1; p < tache_number-12; p++) {
		                            for(int q = p+1; q < tache_number-11; q++) {
		                                for(int r = q+1; r < tache_number-10; r++) {
		                                    for(int s = r+1; s < tache_number-9; s++) {
		                                        for(int t = s+1; t < tache_number-8; t++) {
		                                            for(int u = t+1; u < tache_number-7; u++) {
		                                                for(int v = u+1; v < tache_number-6; v++) {
		                                                    for(int w = v+1; w < tache_number-5; w++) {
		                                                        for(int x = w+1; x < tache_number-4; x++) {
		                                                            for(int y = x+1; y < tache_number-3; y++) {
		                                                                for(int z = y+1; z < tache_number-2; z++) {
			                                                                for(int I = z+1; I < tache_number-1; I++) {
				                                                                for(int J = I+1; J < tache_number; J++) {
					                                                                for(int K = J+1; K < tache_number+1; K++) {
					                                                                    int x1=0, x2=0, x3=0, x4=0, x5=0, x6=0, x7=0, x8=0, x9=0, x10=0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0, x18=0, x19=0, x20=0;
					                                                                    String keys;
			
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J});
					                                                                    x1 = m19.get(keys)[18] + (pour_weight[K-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[K-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K});
					                                                                    x2 = m19.get(keys)[18] + (pour_weight[J-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[J-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K});
					                                                                    x3 = m19.get(keys)[18] + (pour_weight[I-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[I-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K});
					                                                                    x4 = m19.get(keys)[18] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[z-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K});
					                                                                    x5 = m19.get(keys)[18] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[y-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K});
					                                                                    x6 = m19.get(keys)[18] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[x-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K});
					                                                                    x7 = m19.get(keys)[18] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[w-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K});
					                                                                    x8 = m19.get(keys)[18] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[v-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K});
					                                                                    x9 = m19.get(keys)[18] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[u-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K});
					                                                                    x10 = m19.get(keys)[18] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[t-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K});
					                                                                    x11 = m19.get(keys)[18] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[s-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x12 = m19.get(keys)[18] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[r-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x13 = m19.get(keys)[18] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[q-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x14 = m19.get(keys)[18] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[p-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x15 = m19.get(keys)[18] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[o-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x16 = m19.get(keys)[18] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[n-1])));
					                                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x17 = m19.get(keys)[18] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[m-1])));
					                                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x18 = m19.get(keys)[18] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[q-1])));
					                                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x19 = m19.get(keys)[18] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[j-1])));
					                                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    x20 = m19.get(keys)[18] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1] - pour_dueDate[i-1])));

					                                                                    int[] values = new int[20];
					                                                                    if(x1 >= x20 && x2 >= x20 && x3 >= x20 && x4 >= x20 && x5 >= x20 && x6 >= x20 && x7 >= x20 && x8 >= x20 && x9 >= x20 && x10 >= x20 && x11 >= x20 && x12 >= x20 && x13 >= x20 && x14 >= x20 && x15 >= x20 && x16 >= x20 && x17 >= x20 && x18 >= x20 && x19 >= x20)  {
					                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x20};
					                                                                    } else if(x1 >= x19 && x2 >= x19 && x3 >= x19 && x4 >= x19 && x5 >= x19 && x6 >= x19 && x7 >= x19 && x8 >= x19 && x9 >= x19 && x10 >= x19 && x11 >= x19 && x12 >= x19 && x13 >= x19 && x14 >= x19 && x15 >= x19 && x16 >= x19 && x17 >= x19 && x18 >= x19 && x20 >= x19)  {
					                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x19};
					                                                                    } else if(x1 >= x18 && x2 >= x18 && x3 >= x18 && x4 >= x18 && x5 >= x18 && x6 >= x18 && x7 >= x18 && x8 >= x18 && x9 >= x18 && x10 >= x18 && x11 >= x18 && x12 >= x18 && x13 >= x18 && x14 >= x18 && x15 >= x18 && x16 >= x18 && x17 >= x18 && x19 >= x18 && x20 >= x18)  {
					                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x18};
					                                                                    } else if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17 && x18 >= x17 && x19 >= x17 && x20 >= x17)  {
					                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x17};
					                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16 && x18 >= x16 && x19 >= x16 && x20 >= x16)  {
					                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x16};
					                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15 && x18 >= x15 && x19 >= x15 && x20 >= x15)  {
					                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x15};
					                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14 && x18 >= x14 && x19 >= x14 && x20 >= x14)  {
					                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, x14};
					                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13 && x18 >= x13 && x19 >= x13 && x20 >= x13)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, x13};
					                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12 && x18 >= x12 && x19 >= x12 && x20 >= x12)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, x12};
					                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11 && x18 >= x11 && x19 >= x11 && x20 >= x11)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, x11};
					                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10 && x18 >= x10 && x19 >= x10 && x20 >= x10)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, x10};
					                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9 && x18 >= x9 && x19 >= x9 && x20 >= x9)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, x9};
					                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8 && x18 >= x8 && x19 >= x8 && x20 >= x8)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, x8};
					                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7 && x18 >= x7 && x19 >= x7 && x20 >= x7)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, x7};
					                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6 && x18 >= x6 && x19 >= x6 && x20 >= x6)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, x6};
					                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5 && x18 >= x5 && x19 >= x5 && x20 >= x5)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, x5};
					                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4 && x18 >= x4 && x19 >= x4 && x20 >= x4)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, x4};
					                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3 && x18 >= x3 && x19 >= x3 && x20 >= x3)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, x3};
					                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2 && x18 >= x2 && x19 >= x2 && x20 >= x2)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, x2};
					                                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1 && x18 >= x1 && x19 >= x1 && x20 >= x1)  {
					                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, x1};
					                                                                    }
					                                                                    
					                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                    m20.put(keys, values);
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m20 avec son indice
		 */
		int nbTachesCourantes = 20;
		mC.put(nbTachesCourantes, m20);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m20
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 20 taches");
		for(int i = 1; i < tache_number-18; i++) {
		    for(int j = i+1; j < tache_number-17; j++ ) {
		        for(int k = j+1; k < tache_number-16; k++) {
		            for(int m = k+1; m < tache_number-15; m++) {
		                for(int n = m+1; n < tache_number-14; n++) {
		                    for(int o = n+1; o < tache_number-13; o++) {
		                        for(int p = o+1; p < tache_number-12; p++) {
		                            for(int q = p+1; q < tache_number-11; q++) {
		                                for(int r = q+1; r < tache_number-10; r++) {
		                                    for(int s = r+1; s < tache_number-9; s++) {
		                                        for(int t = s+1; t < tache_number-8; t++) {
		                                            for(int u = t+1; u < tache_number-7; u++) {
		                                                for(int v = u+1; v < tache_number-6; v++) {
		                                                    for(int w = v+1; w < tache_number-5; w++) {
		                                                        for(int x = w+1; x < tache_number-4; x++) {
		                                                            for(int y = x+1; y < tache_number-3; y++) {
		                                                                for(int z = y+1; z < tache_number-2; z++) {
			                                                                for(int I = z+1; I < tache_number-1; I++) {
				                                                                for(int J = I+1; J < tache_number; J++) {
					                                                                for(int K = J+1; K < tache_number+1; K++) {
					                                                                	String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
					                                                                	System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, m20.get(keys)[0], m20.get(keys)[1], m20.get(keys)[2], m20.get(keys)[3], m20.get(keys)[4], m20.get(keys)[5], m20.get(keys)[6], m20.get(keys)[7], m20.get(keys)[8], m20.get(keys)[9], m20.get(keys)[10], m20.get(keys)[11], m20.get(keys)[12], m20.get(keys)[13], m20.get(keys)[14], m20.get(keys)[15], m20.get(keys)[16], m20.get(keys)[17], m20.get(keys)[18], m20.get(keys)[19]);
//					                                                                	System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = []\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K);
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public synchronized static void calcM21() {
		/*
		 * Calcule m21
		 */
		HashMap<String, int[]> m20 = mC.get(20);
		HashMap<String, int[]> m21 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-19; i++) {
		    for(int j = i+1; j < tache_number-18; j++ ) {
		        for(int k = j+1; k < tache_number-17; k++) {
		            for(int m = k+1; m < tache_number-16; m++) {
		                for(int n = m+1; n < tache_number-15; n++) {
		                    for(int o = n+1; o < tache_number-14; o++) {
		                        for(int p = o+1; p < tache_number-13; p++) {
		                            for(int q = p+1; q < tache_number-12; q++) {
		                                for(int r = q+1; r < tache_number-11; r++) {
		                                    for(int s = r+1; s < tache_number-10; s++) {
		                                        for(int t = s+1; t < tache_number-9; t++) {
		                                            for(int u = t+1; u < tache_number-8; u++) {
		                                                for(int v = u+1; v < tache_number-7; v++) {
		                                                    for(int w = v+1; w < tache_number-6; w++) {
		                                                        for(int x = w+1; x < tache_number-5; x++) {
		                                                            for(int y = x+1; y < tache_number-4; y++) {
		                                                                for(int z = y+1; z < tache_number-3; z++) {
			                                                                for(int I = z+1; I < tache_number-2; I++) {
				                                                                for(int J = I+1; J < tache_number-1; J++) {
					                                                                for(int K = J+1; K < tache_number; K++) {
						                                                                for(int M = K+1; M < tache_number+1; M++) {
						                                                                    int x1=0, x2=0, x3=0, x4=0, x5=0, x6=0, x7=0, x8=0, x9=0, x10=0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0, x18=0, x19=0, x20=0, x21=0;
						                                                                    String keys;
						                                                                    
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K});
						                                                                    x1 = m20.get(keys)[19] + (pour_weight[M-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[M-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, M});
						                                                                    x2 = m20.get(keys)[19] + (pour_weight[K-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[K-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, M});
						                                                                    x3 = m20.get(keys)[19] + (pour_weight[J-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[J-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, M});
						                                                                    x4 = m20.get(keys)[19] + (pour_weight[I-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[I-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, M});
						                                                                    x5 = m20.get(keys)[19] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[z-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, M});
						                                                                    x6 = m20.get(keys)[19] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[y-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, M});
						                                                                    x7 = m20.get(keys)[19] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[x-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, M});
						                                                                    x8 = m20.get(keys)[19] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[w-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, M});
						                                                                    x9 = m20.get(keys)[19] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[v-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, M});
						                                                                    x10 = m20.get(keys)[19] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[u-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, M});
						                                                                    x11 = m20.get(keys)[19] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[t-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x12 = m20.get(keys)[19] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[s-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x13 = m20.get(keys)[19] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[r-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x14 = m20.get(keys)[19] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[q-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x15 = m20.get(keys)[19] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[p-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x16 = m20.get(keys)[19] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[o-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x17 = m20.get(keys)[19] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[n-1])));
						                                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x18 = m20.get(keys)[19] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[m-1])));
						                                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x19 = m20.get(keys)[19] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[q-1])));
						                                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x20 = m20.get(keys)[19] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[j-1])));
						                                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    x21 = m20.get(keys)[19] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1] - pour_dueDate[i-1])));

						                                                                    int[] values = new int[21];
						                                                                    if(x1 >= x21 && x2 >= x21 && x3 >= x21 && x4 >= x21 && x5 >= x21 && x6 >= x21 && x7 >= x21 && x8 >= x21 && x9 >= x21 && x10 >= x21 && x11 >= x21 && x12 >= x21 && x13 >= x21 && x14 >= x21 && x15 >= x21 && x16 >= x21 && x17 >= x21 && x18 >= x21 && x19 >= x21 && x20 >= x21)  {
						                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x21};
						                                                                    } else if(x1 >= x20 && x2 >= x20 && x3 >= x20 && x4 >= x20 && x5 >= x20 && x6 >= x20 && x7 >= x20 && x8 >= x20 && x9 >= x20 && x10 >= x20 && x11 >= x20 && x12 >= x20 && x13 >= x20 && x14 >= x20 && x15 >= x20 && x16 >= x20 && x17 >= x20 && x18 >= x20 && x19 >= x20 && x21 >= x20)  {
						                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x20};
						                                                                    } else if(x1 >= x19 && x2 >= x19 && x3 >= x19 && x4 >= x19 && x5 >= x19 && x6 >= x19 && x7 >= x19 && x8 >= x19 && x9 >= x19 && x10 >= x19 && x11 >= x19 && x12 >= x19 && x13 >= x19 && x14 >= x19 && x15 >= x19 && x16 >= x19 && x17 >= x19 && x18 >= x19 && x20 >= x19 && x21 >= x19)  {
						                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x19};
						                                                                    } else if(x1 >= x18 && x2 >= x18 && x3 >= x18 && x4 >= x18 && x5 >= x18 && x6 >= x18 && x7 >= x18 && x8 >= x18 && x9 >= x18 && x10 >= x18 && x11 >= x18 && x12 >= x18 && x13 >= x18 && x14 >= x18 && x15 >= x18 && x16 >= x18 && x17 >= x18 && x19 >= x18 && x20 >= x18 && x21 >= x18)  {
						                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x18};
						                                                                    } else if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17 && x18 >= x17 && x19 >= x17 && x20 >= x17 && x21 >= x17)  {
						                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x17};
						                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16 && x18 >= x16 && x19 >= x16 && x20 >= x16 && x21 >= x16)  {
						                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x16};
						                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15 && x18 >= x15 && x19 >= x15 && x20 >= x15 && x21 >= x15)  {
						                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x15};
						                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14 && x18 >= x14 && x19 >= x14 && x20 >= x14 && x21 >= x14)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, M, x14};
						                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13 && x18 >= x13 && x19 >= x13 && x20 >= x13 && x21 >= x13)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, M, x13};
						                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12 && x18 >= x12 && x19 >= x12 && x20 >= x12 && x21 >= x12)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, M, x12};
						                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11 && x18 >= x11 && x19 >= x11 && x20 >= x11 && x21 >= x11)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, M, x11};
						                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10 && x18 >= x10 && x19 >= x10 && x20 >= x10 && x21 >= x10)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, M, x10};
						                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9 && x18 >= x9 && x19 >= x9 && x20 >= x9 && x21 >= x9)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, M, x9};
						                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8 && x18 >= x8 && x19 >= x8 && x20 >= x8 && x21 >= x8)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, M, x8};
						                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7 && x18 >= x7 && x19 >= x7 && x20 >= x7 && x21 >= x7)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, M, x7};
						                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6 && x18 >= x6 && x19 >= x6 && x20 >= x6 && x21 >= x6)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, M, x6};
						                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5 && x18 >= x5 && x19 >= x5 && x20 >= x5 && x21 >= x5)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, M, x5};
						                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4 && x18 >= x4 && x19 >= x4 && x20 >= x4 && x21 >= x4)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, M, x4};
						                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3 && x18 >= x3 && x19 >= x3 && x20 >= x3 && x21 >= x3)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, M, x3};
						                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2 && x18 >= x2 && x19 >= x2 && x20 >= x2 && x21 >= x2)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, M, x2};
						                                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1 && x18 >= x1 && x19 >= x1 && x20 >= x1 && x21 >= x1)  {
						                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, x1};
						                                                                    }
						                                                                    
						                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                    m21.put(keys, values);
						                                                                }
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m21 avec son indice
		 */
		int nbTachesCourantes = 21;
		mC.put(nbTachesCourantes, m21);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m21
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 21 taches");
		for(int i = 1; i < tache_number-19; i++) {
		    for(int j = i+1; j < tache_number-18; j++ ) {
		        for(int k = j+1; k < tache_number-17; k++) {
		            for(int m = k+1; m < tache_number-16; m++) {
		                for(int n = m+1; n < tache_number-15; n++) {
		                    for(int o = n+1; o < tache_number-14; o++) {
		                        for(int p = o+1; p < tache_number-13; p++) {
		                            for(int q = p+1; q < tache_number-12; q++) {
		                                for(int r = q+1; r < tache_number-11; r++) {
		                                    for(int s = r+1; s < tache_number-10; s++) {
		                                        for(int t = s+1; t < tache_number-9; t++) {
		                                            for(int u = t+1; u < tache_number-8; u++) {
		                                                for(int v = u+1; v < tache_number-7; v++) {
		                                                    for(int w = v+1; w < tache_number-6; w++) {
		                                                        for(int x = w+1; x < tache_number-5; x++) {
		                                                            for(int y = x+1; y < tache_number-4; y++) {
		                                                                for(int z = y+1; z < tache_number-3; z++) {
			                                                                for(int I = z+1; I < tache_number-2; I++) {
				                                                                for(int J = I+1; J < tache_number-1; J++) {
					                                                                for(int K = J+1; K < tache_number; K++) {
						                                                                for(int M = K+1; M < tache_number+1; M++) {
						                                                                	String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
						                                                                	System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, m21.get(keys)[0], m21.get(keys)[1], m21.get(keys)[2], m21.get(keys)[3], m21.get(keys)[4], m21.get(keys)[5], m21.get(keys)[6], m21.get(keys)[7], m21.get(keys)[8], m21.get(keys)[9], m21.get(keys)[10], m21.get(keys)[11], m21.get(keys)[12], m21.get(keys)[13], m21.get(keys)[14], m21.get(keys)[15], m21.get(keys)[16], m21.get(keys)[17], m21.get(keys)[18], m21.get(keys)[19], m21.get(keys)[20]);
						                                                                }
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM22() {
		/*
		 * Calcule m22
		 */
		HashMap<String, int[]> m21 = mC.get(21);
		HashMap<String, int[]> m22 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-20; i++) {
		    for(int j = i+1; j < tache_number-19; j++ ) {
		        for(int k = j+1; k < tache_number-18; k++) {
		            for(int m = k+1; m < tache_number-17; m++) {
		                for(int n = m+1; n < tache_number-16; n++) {
		                    for(int o = n+1; o < tache_number-15; o++) {
		                        for(int p = o+1; p < tache_number-14; p++) {
		                            for(int q = p+1; q < tache_number-13; q++) {
		                                for(int r = q+1; r < tache_number-12; r++) {
		                                    for(int s = r+1; s < tache_number-11; s++) {
		                                        for(int t = s+1; t < tache_number-10; t++) {
		                                            for(int u = t+1; u < tache_number-9; u++) {
		                                                for(int v = u+1; v < tache_number-8; v++) {
		                                                    for(int w = v+1; w < tache_number-7; w++) {
		                                                        for(int x = w+1; x < tache_number-6; x++) {
		                                                            for(int y = x+1; y < tache_number-5; y++) {
		                                                                for(int z = y+1; z < tache_number-4; z++) {
			                                                                for(int I = z+1; I < tache_number-3; I++) {
				                                                                for(int J = I+1; J < tache_number-2; J++) {
					                                                                for(int K = J+1; K < tache_number-1; K++) {
						                                                                for(int M = K+1; M < tache_number; M++) {
							                                                                for(int N = M+1; N < tache_number+1; N++) {
							                                                                    int x1=0, x2=0, x3=0, x4=0, x5=0, x6=0, x7=0, x8=0, x9=0, x10=0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0, x18=0, x19=0, x20=0, x21=0, x22=0;
							                                                                    String keys;
					
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M});
							                                                                    x1 = m21.get(keys)[20] + (pour_weight[N-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[N-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, N});
							                                                                    x2 = m21.get(keys)[20] + (pour_weight[M-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[M-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, M, N});
							                                                                    x3 = m21.get(keys)[20] + (pour_weight[K-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[K-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, M, N});
							                                                                    x4 = m21.get(keys)[20] + (pour_weight[J-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[J-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, M, N});
							                                                                    x5 = m21.get(keys)[20] + (pour_weight[I-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[I-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, M, N});
							                                                                    x6 = m21.get(keys)[20] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[z-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, M, N});
							                                                                    x7 = m21.get(keys)[20] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[y-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, M, N});
							                                                                    x8 = m21.get(keys)[20] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[x-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, M, N});
							                                                                    x9 = m21.get(keys)[20] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[w-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, M, N});
							                                                                    x10 = m21.get(keys)[20] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[v-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, M, N});
							                                                                    x11 = m21.get(keys)[20] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[u-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x12 = m21.get(keys)[20] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[t-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x13 = m21.get(keys)[20] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[s-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x14 = m21.get(keys)[20] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[r-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x15 = m21.get(keys)[20] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[q-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x16 = m21.get(keys)[20] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[p-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x17 = m21.get(keys)[20] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[o-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x18 = m21.get(keys)[20] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[n-1])));
							                                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x19 = m21.get(keys)[20] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[m-1])));
							                                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x20 = m21.get(keys)[20] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[k-1])));
							                                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x21 = m21.get(keys)[20] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[j-1])));
							                                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    x22 = m21.get(keys)[20] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1] - pour_dueDate[i-1])));
	
							                                                                    int[] values = new int[22];
							                                                                    if(x1 >= x22 && x2 >= x22 && x3 >= x22 && x4 >= x22 && x5 >= x22 && x6 >= x22 && x7 >= x22 && x8 >= x22 && x9 >= x22 && x10 >= x22 && x11 >= x22 && x12 >= x22 && x13 >= x22 && x14 >= x22 && x15 >= x22 && x16 >= x22 && x17 >= x22 && x18 >= x22 && x19 >= x22 && x20 >= x22 && x21 >= x22)  {
							                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x22};
							                                                                    } else if(x1 >= x21 && x2 >= x21 && x3 >= x21 && x4 >= x21 && x5 >= x21 && x6 >= x21 && x7 >= x21 && x8 >= x21 && x9 >= x21 && x10 >= x21 && x11 >= x21 && x12 >= x21 && x13 >= x21 && x14 >= x21 && x15 >= x21 && x16 >= x21 && x17 >= x21 && x18 >= x21 && x19 >= x21 && x20 >= x21 && x22 >= x21)  {
							                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x21};
							                                                                    } else if(x1 >= x20 && x2 >= x20 && x3 >= x20 && x4 >= x20 && x5 >= x20 && x6 >= x20 && x7 >= x20 && x8 >= x20 && x9 >= x20 && x10 >= x20 && x11 >= x20 && x12 >= x20 && x13 >= x20 && x14 >= x20 && x15 >= x20 && x16 >= x20 && x17 >= x20 && x18 >= x20 && x19 >= x20 && x21 >= x20 && x22 >= x20)  {
							                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x20};
							                                                                    } else if(x1 >= x19 && x2 >= x19 && x3 >= x19 && x4 >= x19 && x5 >= x19 && x6 >= x19 && x7 >= x19 && x8 >= x19 && x9 >= x19 && x10 >= x19 && x11 >= x19 && x12 >= x19 && x13 >= x19 && x14 >= x19 && x15 >= x19 && x16 >= x19 && x17 >= x19 && x18 >= x19 && x20 >= x19 && x21 >= x19 && x22 >= x19)  {
							                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x19};
							                                                                    } else if(x1 >= x18 && x2 >= x18 && x3 >= x18 && x4 >= x18 && x5 >= x18 && x6 >= x18 && x7 >= x18 && x8 >= x18 && x9 >= x18 && x10 >= x18 && x11 >= x18 && x12 >= x18 && x13 >= x18 && x14 >= x18 && x15 >= x18 && x16 >= x18 && x17 >= x18 && x19 >= x18 && x20 >= x18 && x21 >= x18 && x22 >= x18)  {
							                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x18};
							                                                                    } else if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17 && x18 >= x17 && x19 >= x17 && x20 >= x17 && x21 >= x17 && x22 >= x17)  {
							                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x17};
							                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16 && x18 >= x16 && x19 >= x16 && x20 >= x16 && x21 >= x16 && x22 >= x16)  {
							                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x16};
							                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15 && x18 >= x15 && x19 >= x15 && x20 >= x15 && x21 >= x15 && x22 >= x15)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x15};
							                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14 && x18 >= x14 && x19 >= x14 && x20 >= x14 && x21 >= x14 && x22 >= x14)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, M, N, x14};
							                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13 && x18 >= x13 && x19 >= x13 && x20 >= x13 && x21 >= x13 && x22 >= x13)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, M, N, x13};
							                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12 && x18 >= x12 && x19 >= x12 && x20 >= x12 && x21 >= x12 && x22 >= x12)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, M, N, x12};
							                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11 && x18 >= x11 && x19 >= x11 && x20 >= x11 && x21 >= x11 && x22 >= x11)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, M, N, x11};
							                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10 && x18 >= x10 && x19 >= x10 && x20 >= x10 && x21 >= x10 && x22 >= x10)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, M, N, x10};
							                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9 && x18 >= x9 && x19 >= x9 && x20 >= x9 && x21 >= x9 && x22 >= x9)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, M, N, x9};
							                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8 && x18 >= x8 && x19 >= x8 && x20 >= x8 && x21 >= x8 && x22 >= x8)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, M, N, x8};
							                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7 && x18 >= x7 && x19 >= x7 && x20 >= x7 && x21 >= x7 && x22 >= x7)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, M, N, x7};
							                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6 && x18 >= x6 && x19 >= x6 && x20 >= x6 && x21 >= x6 && x22 >= x6)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, M, N, x6};
							                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5 && x18 >= x5 && x19 >= x5 && x20 >= x5 && x21 >= x5 && x22 >= x5)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, M, N, x5};
							                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4 && x18 >= x4 && x19 >= x4 && x20 >= x4 && x21 >= x4 && x22 >= x4)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, M, N, x4};
							                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3 && x18 >= x3 && x19 >= x3 && x20 >= x3 && x21 >= x3 && x22 >= x3)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, M, N, x3};
							                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2 && x18 >= x2 && x19 >= x2 && x20 >= x2 && x21 >= x2 && x22 >= x2)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, N, x2};
							                                                                    }else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1 && x18 >= x1 && x19 >= x1 && x20 >= x1 && x21 >= x1 && x22 >= x1)  {
							                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, x1};
							                                                                    }
							                                                                    
							                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                    m22.put(keys, values);
							                                                                }
						                                                                }
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m22 avec son indice
		 */
		int nbTachesCourantes = 22;
		mC.put(nbTachesCourantes, m22);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m22
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 22 taches");
		for(int i = 1; i < tache_number-20; i++) {
		    for(int j = i+1; j < tache_number-19; j++ ) {
		        for(int k = j+1; k < tache_number-18; k++) {
		            for(int m = k+1; m < tache_number-17; m++) {
		                for(int n = m+1; n < tache_number-16; n++) {
		                    for(int o = n+1; o < tache_number-15; o++) {
		                        for(int p = o+1; p < tache_number-14; p++) {
		                            for(int q = p+1; q < tache_number-13; q++) {
		                                for(int r = q+1; r < tache_number-12; r++) {
		                                    for(int s = r+1; s < tache_number-11; s++) {
		                                        for(int t = s+1; t < tache_number-10; t++) {
		                                            for(int u = t+1; u < tache_number-9; u++) {
		                                                for(int v = u+1; v < tache_number-8; v++) {
		                                                    for(int w = v+1; w < tache_number-7; w++) {
		                                                        for(int x = w+1; x < tache_number-6; x++) {
		                                                            for(int y = x+1; y < tache_number-5; y++) {
		                                                                for(int z = y+1; z < tache_number-4; z++) {
			                                                                for(int I = z+1; I < tache_number-3; I++) {
				                                                                for(int J = I+1; J < tache_number-2; J++) {
					                                                                for(int K = J+1; K < tache_number-1; K++) {
						                                                                for(int M = K+1; M < tache_number; M++) {
							                                                                for(int N = M+1; N < tache_number+1; N++) {
							                                                                	String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
							                                                                	System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, m22.get(keys)[0], m22.get(keys)[1], m22.get(keys)[2], m22.get(keys)[3], m22.get(keys)[4], m22.get(keys)[5], m22.get(keys)[6], m22.get(keys)[7], m22.get(keys)[8], m22.get(keys)[9], m22.get(keys)[10], m22.get(keys)[11], m22.get(keys)[12], m22.get(keys)[13], m22.get(keys)[14], m22.get(keys)[15], m22.get(keys)[16], m22.get(keys)[17], m22.get(keys)[18], m22.get(keys)[19], m22.get(keys)[20], m22.get(keys)[21]);
							                                                                }
						                                                                }
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	public synchronized static void calcM23() {
		/*
		 * Calcule m23
		 */
		HashMap<String, int[]> m22 = mC.get(22);
		HashMap<String, int[]> m23 = new HashMap<String, int[]>();
		for(int i = 1; i < tache_number-21; i++) {
		    for(int j = i+1; j < tache_number-20; j++ ) {
		        for(int k = j+1; k < tache_number-19; k++) {
		            for(int m = k+1; m < tache_number-18; m++) {
		                for(int n = m+1; n < tache_number-17; n++) {
		                    for(int o = n+1; o < tache_number-16; o++) {
		                        for(int p = o+1; p < tache_number-15; p++) {
		                            for(int q = p+1; q < tache_number-14; q++) {
		                                for(int r = q+1; r < tache_number-13; r++) {
		                                    for(int s = r+1; s < tache_number-12; s++) {
		                                        for(int t = s+1; t < tache_number-11; t++) {
		                                            for(int u = t+1; u < tache_number-10; u++) {
		                                                for(int v = u+1; v < tache_number-9; v++) {
		                                                    for(int w = v+1; w < tache_number-8; w++) {
		                                                        for(int x = w+1; x < tache_number-7; x++) {
		                                                            for(int y = x+1; y < tache_number-6; y++) {
		                                                                for(int z = y+1; z < tache_number-5; z++) {
			                                                                for(int I = z+1; I < tache_number-4; I++) {
				                                                                for(int J = I+1; J < tache_number-3; J++) {
					                                                                for(int K = J+1; K < tache_number-2; K++) {
						                                                                for(int M = K+1; M < tache_number-1; M++) {
							                                                                for(int N = M+1; N < tache_number; N++) {
								                                                                for(int O = N+1; O < tache_number+1; O++) {
								                                                                    int x1=0, x2=0, x3=0, x4=0, x5=0, x6=0, x7=0, x8=0, x9=0, x10=0, x11=0, x12=0, x13=0, x14=0, x15=0, x16=0, x17=0, x18=0, x19=0, x20=0, x21=0, x22=0, x23=0;
								                                                                    String keys;
						
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N});
								                                                                    x1 = m22.get(keys)[21] + (pour_weight[O-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[O-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, O});
								                                                                    x2 = m22.get(keys)[21] + (pour_weight[N-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[N-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, N, O});
								                                                                    x3 = m22.get(keys)[21] + (pour_weight[M-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[M-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, M, N, O});
								                                                                    x4 = m22.get(keys)[21] + (pour_weight[K-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[K-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, M, N, O});
								                                                                    x5 = m22.get(keys)[21] + (pour_weight[J-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[J-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, M, N, O});
								                                                                    x6 = m22.get(keys)[21] + (pour_weight[I-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[I-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, M, N, O});
								                                                                    x7 = m22.get(keys)[21] + (pour_weight[z-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[z-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, M, N, O});
								                                                                    x8 = m22.get(keys)[21] + (pour_weight[y-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[y-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, M, N, O});
								                                                                    x9 = m22.get(keys)[21] + (pour_weight[x-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[x-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, M, N, O});
								                                                                    x10 = m22.get(keys)[21] + (pour_weight[w-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[w-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, M, N, O});
								                                                                    x11 = m22.get(keys)[21] + (pour_weight[v-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[v-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x12 = m22.get(keys)[21] + (pour_weight[u-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[u-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x13 = m22.get(keys)[21] + (pour_weight[t-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[t-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x14 = m22.get(keys)[21] + (pour_weight[s-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[s-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x15 = m22.get(keys)[21] + (pour_weight[r-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[r-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x16 = m22.get(keys)[21] + (pour_weight[q-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[q-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x17 = m22.get(keys)[21] + (pour_weight[p-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[p-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x18 = m22.get(keys)[21] + (pour_weight[o-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[o-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x19 = m22.get(keys)[21] + (pour_weight[n-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[n-1])));
								                                                                    keys = TabToString(new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x20 = m22.get(keys)[21] + (pour_weight[m-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[m-1])));
								                                                                    keys = TabToString(new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x21 = m22.get(keys)[21] + (pour_weight[k-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[k-1])));
								                                                                    keys = TabToString(new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x22 = m22.get(keys)[21] + (pour_weight[j-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[j-1])));
								                                                                    keys = TabToString(new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    x23 = m22.get(keys)[21] + (pour_weight[i-1] * max(0, (pour_periode[i-1]+pour_periode[j-1]+pour_periode[k-1]+pour_periode[m-1]+pour_periode[n-1]+pour_periode[o-1]+pour_periode[p-1]+pour_periode[q-1]+pour_periode[r-1]+pour_periode[s-1]+pour_periode[t-1]+pour_periode[u-1]+pour_periode[v-1]+pour_periode[w-1]+pour_periode[x-1]+pour_periode[y-1]+pour_periode[z-1]+pour_periode[I-1]+pour_periode[J-1]+pour_periode[K-1]+pour_periode[M-1]+pour_periode[N-1]+pour_periode[O-1] - pour_dueDate[i-1])));

								                                                                    int[] values = new int[23];
								                                                                    if(x1 >= x23 && x2 >= x23 && x3 >= x23 && x4 >= x23 && x5 >= x23 && x6 >= x23 && x7 >= x23 && x8 >= x23 && x9 >= x23 && x10 >= x23 && x11 >= x23 && x12 >= x23 && x13 >= x23 && x14 >= x23 && x15 >= x23 && x16 >= x23 && x17 >= x23 && x18 >= x23 && x19 >= x23 && x20 >= x23 && x21 >= x23 && x22 >= x23)  {
								                                                                        values = new int[]{j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x23};
								                                                                    } else if(x1 >= x22 && x2 >= x22 && x3 >= x22 && x4 >= x22 && x5 >= x22 && x6 >= x22 && x7 >= x22 && x8 >= x22 && x9 >= x22 && x10 >= x22 && x11 >= x22 && x12 >= x22 && x13 >= x22 && x14 >= x22 && x15 >= x22 && x16 >= x22 && x17 >= x22 && x18 >= x22 && x19 >= x22 && x20 >= x22 && x21 >= x22 && x23 >= x22)  {
								                                                                        values = new int[]{i, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x22};
								                                                                    } else if(x1 >= x21 && x2 >= x21 && x3 >= x21 && x4 >= x21 && x5 >= x21 && x6 >= x21 && x7 >= x21 && x8 >= x21 && x9 >= x21 && x10 >= x21 && x11 >= x21 && x12 >= x21 && x13 >= x21 && x14 >= x21 && x15 >= x21 && x16 >= x21 && x17 >= x21 && x18 >= x21 && x19 >= x21 && x20 >= x21 && x22 >= x21 && x23 >= x21)  {
								                                                                        values = new int[]{i, j, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x21};
								                                                                    } else if(x1 >= x20 && x2 >= x20 && x3 >= x20 && x4 >= x20 && x5 >= x20 && x6 >= x20 && x7 >= x20 && x8 >= x20 && x9 >= x20 && x10 >= x20 && x11 >= x20 && x12 >= x20 && x13 >= x20 && x14 >= x20 && x15 >= x20 && x16 >= x20 && x17 >= x20 && x18 >= x20 && x19 >= x20 && x21 >= x20 && x22 >= x20 && x23 >= x20)  {
								                                                                        values = new int[]{i, j, k, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x20};
								                                                                    } else if(x1 >= x19 && x2 >= x19 && x3 >= x19 && x4 >= x19 && x5 >= x19 && x6 >= x19 && x7 >= x19 && x8 >= x19 && x9 >= x19 && x10 >= x19 && x11 >= x19 && x12 >= x19 && x13 >= x19 && x14 >= x19 && x15 >= x19 && x16 >= x19 && x17 >= x19 && x18 >= x19 && x20 >= x19 && x21 >= x19 && x22 >= x19 && x23 >= x19)  {
								                                                                        values = new int[]{i, j, k, m, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x19};
								                                                                    } else if(x1 >= x18 && x2 >= x18 && x3 >= x18 && x4 >= x18 && x5 >= x18 && x6 >= x18 && x7 >= x18 && x8 >= x18 && x9 >= x18 && x10 >= x18 && x11 >= x18 && x12 >= x18 && x13 >= x18 && x14 >= x18 && x15 >= x18 && x16 >= x18 && x17 >= x18 && x19 >= x18 && x20 >= x18 && x21 >= x18 && x22 >= x18 && x23 >= x18)  {
								                                                                        values = new int[]{i, j, k, m, n, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x18};
								                                                                    } else if(x1 >= x17 && x2 >= x17 && x3 >= x17 && x4 >= x17 && x5 >= x17 && x6 >= x17 && x7 >= x17 && x8 >= x17 && x9 >= x17 && x10 >= x17 && x11 >= x17 && x12 >= x17 && x13 >= x17 && x14 >= x17 && x15 >= x17 && x16 >= x17 && x18 >= x17 && x19 >= x17 && x20 >= x17 && x21 >= x17 && x22 >= x17 && x23 >= x17)  {
								                                                                        values = new int[]{i, j, k, m, n, o, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x17};
								                                                                    } else if(x1 >= x16 && x2 >= x16 && x3 >= x16 && x4 >= x16 && x5 >= x16 && x6 >= x16 && x7 >= x16 && x8 >= x16 && x9 >= x16 && x10 >= x16 && x11 >= x16 && x12 >= x16 && x13 >= x16 && x14 >= x16 && x15 >= x16 && x17 >= x16 && x18 >= x16 && x19 >= x16 && x20 >= x16 && x21 >= x16 && x22 >= x16 && x23 >= x16)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x16};
								                                                                    } else if(x1 >= x15 && x2 >= x15 && x3 >= x15 && x4 >= x15 && x5 >= x15 && x6 >= x15 && x7 >= x15 && x8 >= x15 && x9 >= x15 && x10 >= x15 && x11 >= x15 && x12 >= x15 && x13 >= x15 && x14 >= x15 && x16 >= x15 && x17 >= x15 && x18 >= x15 && x19 >= x15 && x20 >= x15 && x21 >= x15 && x22 >= x15 && x23 >= x15)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, s, t, u, v, w, x, y, z, I, J, K, M, N, O, x15};
								                                                                    } else if(x1 >= x14 && x2 >= x14 && x3 >= x14 && x4 >= x14 && x5 >= x14 && x6 >= x14 && x7 >= x14 && x8 >= x14 && x9 >= x14 && x10 >= x14 && x11 >= x14 && x12 >= x14 && x13 >= x14 && x15 >= x14 && x16 >= x14 && x17 >= x14 && x18 >= x14 && x19 >= x14 && x20 >= x14 && x21 >= x14 && x22 >= x14 && x23 >= x14)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, t, u, v, w, x, y, z, I, J, K, M, N, O, x14};
								                                                                    } else if(x1 >= x13 && x2 >= x13 && x3 >= x13 && x4 >= x13 && x5 >= x13 && x6 >= x13 && x7 >= x13 && x8 >= x13 && x9 >= x13 && x10 >= x13 && x11 >= x13 && x12 >= x13 && x14 >= x13 && x15 >= x13 && x16 >= x13 && x17 >= x13 && x18 >= x13 && x19 >= x13 && x20 >= x13 && x21 >= x13 && x22 >= x13 && x23 >= x13)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, u, v, w, x, y, z, I, J, K, M, N, O, x13};
								                                                                    } else if(x1 >= x12 && x2 >= x12 && x3 >= x12 && x4 >= x12 && x5 >= x12 && x6 >= x12 && x7 >= x12 && x8 >= x12 && x9 >= x12 && x10 >= x12 && x11 >= x12 && x13 >= x12 && x14 >= x12 && x15 >= x12 && x16 >= x12 && x17 >= x12 && x18 >= x12 && x19 >= x12 && x20 >= x12 && x21 >= x12 && x22 >= x12 && x23 >= x12)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, v, w, x, y, z, I, J, K, M, N, O, x12};
								                                                                    } else if(x1 >= x11 && x2 >= x11 && x3 >= x11 && x4 >= x11 && x5 >= x11 && x6 >= x11 && x7 >= x11 && x8 >= x11 && x9 >= x11 && x10 >= x11 && x12 >= x11 && x13 >= x11 && x14 >= x11 && x15 >= x11 && x16 >= x11 && x17 >= x11 && x18 >= x11 && x19 >= x11 && x20 >= x11 && x21 >= x11 && x22 >= x11 && x23 >= x11)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, w, x, y, z, I, J, K, M, N, O, x11};
								                                                                    } else if(x1 >= x10 && x2 >= x10 && x3 >= x10 && x4 >= x10 && x5 >= x10 && x6 >= x10 && x7 >= x10 && x8 >= x10 && x9 >= x10 && x11 >= x10 && x12 >= x10 && x13 >= x10 && x14 >= x10 && x15 >= x10 && x16 >= x10 && x17 >= x10 && x18 >= x10 && x19 >= x10 && x20 >= x10 && x21 >= x10 && x22 >= x10 && x23 >= x10)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, x, y, z, I, J, K, M, N, O, x10};
								                                                                    } else if(x1 >= x9 && x2 >= x9 && x3 >= x9 && x4 >= x9 && x5 >= x9 && x6 >= x9 && x7 >= x9 && x8 >= x9 && x10 >= x9 && x11 >= x9 && x12 >= x9 && x13 >= x9 && x14 >= x9 && x15 >= x9 && x16 >= x9 && x17 >= x9 && x18 >= x9 && x19 >= x9 && x20 >= x9 && x21 >= x9 && x22 >= x9 && x23 >= x9)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, y, z, I, J, K, M, N, O, x9};
								                                                                    } else if(x1 >= x8 && x2 >= x8 && x3 >= x8 && x4 >= x8 && x5 >= x8 && x6 >= x8 && x7 >= x8 && x9 >= x8 && x10 >= x8 && x11 >= x8 && x12 >= x8 && x13 >= x8 && x14 >= x8 && x15 >= x8 && x16 >= x8 && x17 >= x8 && x18 >= x8 && x19 >= x8 && x20 >= x8 && x21 >= x8 && x22 >= x8 && x23 >= x8)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, z, I, J, K, M, N, O, x8};
								                                                                    } else if(x1 >= x7 && x2 >= x7 && x3 >= x7 && x4 >= x7 && x5 >= x7 && x6 >= x7 && x8 >= x7 && x9 >= x7 && x10 >= x7 && x11 >= x7 && x12 >= x7 && x13 >= x7 && x14 >= x7 && x15 >= x7 && x16 >= x7 && x17 >= x7 && x18 >= x7 && x19 >= x7 && x20 >= x7 && x21 >= x7 && x22 >= x7 && x23 >= x7)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, I, J, K, M, N, O, x7};
								                                                                    } else if(x1 >= x6 && x2 >= x6 && x3 >= x6 && x4 >= x6 && x5 >= x6 && x7 >= x6 && x8 >= x6 && x9 >= x6 && x10 >= x6 && x11 >= x6 && x12 >= x6 && x13 >= x6 && x14 >= x6 && x15 >= x6 && x16 >= x6 && x17 >= x6 && x18 >= x6 && x19 >= x6 && x20 >= x6 && x21 >= x6 && x22 >= x6 && x23 >= x6)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, J, K, M, N, O, x6};
								                                                                    } else if(x1 >= x5 && x2 >= x5 && x3 >= x5 && x4 >= x5 && x6 >= x5 && x7 >= x5 && x8 >= x5 && x9 >= x5 && x10 >= x5 && x11 >= x5 && x12 >= x5 && x13 >= x5 && x14 >= x5 && x15 >= x5 && x16 >= x5 && x17 >= x5 && x18 >= x5 && x19 >= x5 && x20 >= x5 && x21 >= x5 && x22 >= x5 && x23 >= x5)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, K, M, N, O, x5};
								                                                                    } else if(x1 >= x4 && x2 >= x4 && x3 >= x4 && x5 >= x4 && x6 >= x4 && x7 >= x4 && x8 >= x4 && x9 >= x4 && x10 >= x4 && x11 >= x4 && x12 >= x4 && x13 >= x4 && x14 >= x4 && x15 >= x4 && x16 >= x4 && x17 >= x4 && x18 >= x4 && x19 >= x4 && x20 >= x4 && x21 >= x4 && x22 >= x4 && x23 >= x4)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, M, N, O, x4};
								                                                                    } else if(x1 >= x3 && x2 >= x3 && x4 >= x3 && x5 >= x3 && x6 >= x3 && x7 >= x3 && x8 >= x3 && x9 >= x3 && x10 >= x3 && x11 >= x3 && x12 >= x3 && x13 >= x3 && x14 >= x3 && x15 >= x3 && x16 >= x3 && x17 >= x3 && x18 >= x3 && x19 >= x3 && x20 >= x3 && x21 >= x3 && x22 >= x3 && x23 >= x3)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, N, O, x3};
								                                                                    } else if(x1 >= x2 && x3 >= x2 && x4 >= x2 && x5 >= x2 && x6 >= x2 && x7 >= x2 && x8 >= x2 && x9 >= x2 && x10 >= x2 && x11 >= x2 && x12 >= x2 && x13 >= x2 && x14 >= x2 && x15 >= x2 && x16 >= x2 && x17 >= x2 && x18 >= x2 && x19 >= x2 && x20 >= x2 && x21 >= x2 && x22 >= x2 && x23 >= x2)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, O, x2};
								                                                                    } else if(x2 >= x1 && x3 >= x1 && x4 >= x1 && x5 >= x1 && x6 >= x1 && x7 >= x1 && x8 >= x1 && x9 >= x1 && x10 >= x1 && x11 >= x1 && x12 >= x1 && x13 >= x1 && x14 >= x1 && x15 >= x1 && x16 >= x1 && x17 >= x1 && x18 >= x1 && x19 >= x1 && x20 >= x1 && x21 >= x1 && x22 >= x1 && x23 >= x1)  {
								                                                                        values = new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, x1};
								                                                                    }

								                                                                    keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                    m23.put(keys, values);
								                                                                }
							                                                                }
						                                                                }
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}

		/*
		 * Correspondre la m23 avec son indice
		 */
		int nbTachesCourantes = 23;
		mC.put(nbTachesCourantes, m23);
		
		if(!affichierCalc && nbTachesCourantes != tache_number) {
			return;
		}

		/*
		 * Affichage m23
		 */
		System.out.println();
		System.out.println();
		
		System.out.println("Valeurs pour 23 taches");
		for(int i = 1; i < tache_number-21; i++) {
		    for(int j = i+1; j < tache_number-20; j++ ) {
		        for(int k = j+1; k < tache_number-19; k++) {
		            for(int m = k+1; m < tache_number-18; m++) {
		                for(int n = m+1; n < tache_number-17; n++) {
		                    for(int o = n+1; o < tache_number-16; o++) {
		                        for(int p = o+1; p < tache_number-15; p++) {
		                            for(int q = p+1; q < tache_number-14; q++) {
		                                for(int r = q+1; r < tache_number-13; r++) {
		                                    for(int s = r+1; s < tache_number-12; s++) {
		                                        for(int t = s+1; t < tache_number-11; t++) {
		                                            for(int u = t+1; u < tache_number-10; u++) {
		                                                for(int v = u+1; v < tache_number-9; v++) {
		                                                    for(int w = v+1; w < tache_number-8; w++) {
		                                                        for(int x = w+1; x < tache_number-7; x++) {
		                                                            for(int y = x+1; y < tache_number-6; y++) {
		                                                                for(int z = y+1; z < tache_number-5; z++) {
			                                                                for(int I = z+1; I < tache_number-4; I++) {
				                                                                for(int J = I+1; J < tache_number-3; J++) {
					                                                                for(int K = J+1; K < tache_number-2; K++) {
						                                                                for(int M = K+1; M < tache_number-1; M++) {
							                                                                for(int N = M+1; N < tache_number; N++) {
								                                                                for(int O = N+1; O < tache_number+1; O++) {
								                                                                	String keys = TabToString(new int[]{i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O});
								                                                                	System.out.printf("f({%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d}) = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d]\n", i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z, I, J, K, M, N, O, m23.get(keys)[0], m23.get(keys)[1], m23.get(keys)[2], m23.get(keys)[3], m23.get(keys)[4], m23.get(keys)[5], m23.get(keys)[6], m23.get(keys)[7], m23.get(keys)[8], m23.get(keys)[9], m23.get(keys)[10], m23.get(keys)[11], m23.get(keys)[12], m23.get(keys)[13], m23.get(keys)[14], m23.get(keys)[15], m23.get(keys)[16], m23.get(keys)[17], m23.get(keys)[18], m23.get(keys)[19], m23.get(keys)[20], m23.get(keys)[21], m23.get(keys)[22]);
								                                                                }
							                                                                }
						                                                                }
					                                                                }
				                                                                }
			                                                                }
		                                                                }
		                                                            }
		                                                        }
		                                                    }
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
	}
	
	
	public static void main(String[] args) {
		
		// On recupere le nombres de taches qui doit etre entre 1 et 23
		
	    do {
	    	System.out.println("Le nombre des taches doit etre strictement entre 1 et 24:");
			System.out.println("Saisissez le nombre des taches que vous voulez : ");
			tache_number = (new  Scanner(System.in)).nextInt();
	    } while (tache_number > 23 || tache_number < 1);
	    
		// On cree n taches avec toujours le due_date egale a zero
		for(int i= 0; i < tache_number; i++) {
			taches.add(new Tache());
		}
		
		// On recupere la somme des periodes des taches
		int sommePeriodes = 0;
		for (int i = 0; i < taches .size(); i++) {
			sommePeriodes += taches.get(i).getPeriode();
		}
		// On genere le dueDate aleatroirement en fonction du nombre total des taches
		for (int i = 0; i < taches .size(); i++) {
			taches.get(i).generateDueDate(sommePeriodes);
		}
		
	    
		/*
		 * Definition des tableaux des valeurs(priodes, weights, due dates)
		 */
		definitionTableauxValeurs();
		
		/*
		 * Affichage initiale des differentes valeurs generees
		 */
		affichageInitiale(sommePeriodes);
		
		/*
		 * Calcule valeurs initiales
		 */
		calcValeursInitiales();
		
		/*
		 * Calcule valeur optimale de la fonction objectif
		 */
		calcValeurOptimaleFonction();
	
		/*
		 * Calcule m2
		 */
		calcM2();
		
		/*
		 * Calcule m3
		 */
		calcM3();
		
		/*
		 * Calcule m4
		 */
		calcM4();

		/*
		 * Calcule m5
		 */
		calcM5();

		/*
		 * Calcule m6
		 */
		calcM6();

		/*
		 * Calcule m7
		 */
		calcM7();
		
		/*
		 * Calcule m8
		 */
		calcM8();
		
		/*
		 * Calcule m9
		 */
		calcM9();
	   
		/*
		 * Calcule M10
		 */
		calcM10();

		/*
		 * Calcule M11
		 */
		calcM11();

		/*
		 * Calcule M12
		 */
		calcM12();

		/*
		 * Calcule M13
		 */
		calcM13();

		/*
		 * Calcule M14
		 */
		calcM14();

		/*
		 * Calcule M15
		 */
		calcM15();

		/*
		 * Calcule M16
		 */
		calcM16();

		/*
		 * Calcule M17
		 */
		calcM17();

		/*
		 * Calcule M18
		 */
		calcM18();

		/*
		 * Calcule M19
		 */
		calcM19();

		/* qui
		 * Calcule M20
		 */
		calcM20();

		/*
		 * Calcule M21
		 */
		calcM21();

		/*
		 * Calcule M22
		 */
		calcM22();

		/*
		 * Calcule M23
		 */
		calcM23();
		
		/*
		* Calcule Ordonencement
		*/
		trouverOrdre(tache_number, new int[]{});
		   
	}
	
	// functions
	
	static synchronized void trouverOrdre(int nT, int[] var) {
		if(nT == tache_number) {
			int sum = 1;
			var = new int[tache_number];
			for(int i = 0; i < tache_number; i++) {
			    var[i] = sum;
			    sum++;
			}
		}
		if(nT == 1) {
			ordonencement.add(var[0]);
			System.out.println("----------ORDONENCEMENT OPTIMAL : " + ordonencement.toString());
			
		} else if( nT != 0) {
			
			HashMap<String, int[]> M = mC.get(nT);
			
			int[] b;
			int sum = 1;
			
			b = M.get(TabToString(var));
			
			int[] var1 = new int[nT-1];
			for(int i=0; i < var1.length; i++) {
				var1[i] = b[i];
			}
			
			ArrayList<Integer> gaga = new ArrayList<Integer>();
	
			for(int j = 0; j < var.length; j++) {
				gaga.add(var[j]);
			}
			
			for(int k = 0; k < var1.length; k++) {
				int indice = gaga.indexOf(var1[k]);
				gaga.remove(indice);
			}
			
			ordonencement.add(gaga.get(0));
			
			trouverOrdre(nT-1, var1);
		}

		
	}
	
	
	public static int max(int x,int y){
		if( x > y) return x;
		else return y;
	}
	public static int min(int x,int y){
		if(x > y) return y;
		else return x;
	}
	
	static String TabToString(int[] keys) {
		String stringKey = "";
		for(int i = 0; i < keys.length; i++) {
			stringKey += keys[i];
			if (i+1 != keys.length) {
				stringKey += ",";
			}
		}
		return stringKey;
	}
	

}
