package com.medisupply.route.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Tsp3OptService {

  @Value("${reco.max-sweeps:200}")
  private int maxSweeps;

  @Value("${reco.max-improvements:10000}")
  private int maxImprovements;

  public List<String> solveTour(String startEnd, List<String> mustVisit) {
    long t0 = System.nanoTime();

    List<String> sorted = new ArrayList<>(new LinkedHashSet<>(mustVisit));
    int n = sorted.size() + 2;
    int[] tour = new int[n];
    tour[0] = MatrixStatic.idx(startEnd);
    for (int i=0;i<sorted.size();i++) tour[i+1] = MatrixStatic.idx(sorted.get(i));
    tour[n-1] = MatrixStatic.idx(startEnd);

    int sweeps = 0;
    int improvements = 0;
    boolean changed = true;
    while (changed && sweeps < maxSweeps && improvements < maxImprovements) {
      sweeps++;
      changed = false;

      double bestGain = 0;
      int bi=-1,bj=-1,bk=-1, bestCase=0;

      for (int i=1;i<n-3;i++){
        int Ai = tour[i-1], Bi = tour[i];
        for (int j=i+1;j<n-2;j++){
          int Cj = tour[j], Dj = tour[j+1];
          for (int k=j+1;k<n-1;k++){
            int Ek = tour[k], Fk = tour[k+1];

            double base = d(Ai,Bi) + d(Cj,Dj) + d(Ek,Fk);

            double[] delta = new double[7];
            delta[1] = (d(Ai,Cj)+d(Bi,Dj)+d(Ek,Fk)) - base;
            delta[2] = (d(Ai,Bi)+d(Cj,Ek)+d(Dj,Fk)) - base;
            delta[3] = (d(Ai,Cj)+d(Bi,Ek)+d(Dj,Fk)) - base;
            delta[4] = (d(Ai,Dj)+d(Ek,Bi)+d(Cj,Fk)) - base;
            delta[5] = (d(Ai,Dj)+d(Ek,Cj)+d(Bi,Fk)) - base;
            delta[6] = (d(Ai,Ek)+d(Cj,Bi)+d(Dj,Fk)) - base;

            for (int cs=1; cs<=6; cs++){
              if (delta[cs] < bestGain - 1e-12 ||
                  (Math.abs(delta[cs]-bestGain) <= 1e-12 && tie(i,j,k,cs,bi,bj,bk,bestCase))) {
                bestGain = delta[cs]; bi=i; bj=j; bk=k; bestCase=cs;
              }
            }
          }
        }
      }

      if (bestGain < -1e-12) {
        applyCase(tour, bi, bj, bk, bestCase);
        improvements++;
        changed = true;
      }
    }

    long t1 = System.nanoTime();
    System.out.printf(Locale.ROOT, "3opt: n=%d, sweeps=%d, improvements=%d, time=%.3f ms%n",
        n, sweeps, improvements, (t1 - t0)/1e6);

    List<String> out = new ArrayList<>(n);
    for (int v : tour) out.add(MatrixStatic.NODES.get(v));
    return out;
  }

  private double d(int a, int b) { return MatrixStatic.d(a,b); }

  private boolean tie(int i,int j,int k,int cs,int bi,int bj,int bk,int bc){
    String s1 = i+":"+j+":"+k+":"+cs, s2 = bi+":"+bj+":"+bk+":"+bc;
    return s1.compareTo(s2) < 0;
  }

  private void applyCase(int[] t,int i,int j,int k,int cs){
    switch (cs){
      case 1 -> reverse(t,i,j-1);
      case 2 -> reverse(t,j,k-1);
      case 3 -> { reverse(t,i,j-1); reverse(t,j,k-1); }
      case 4 -> reconcat(t,i,j,k,true,true);
      case 5 -> reconcatSwap(t,i,j,k);
      case 6 -> reconcat(t,i,j,k,true,false);
    }
  }

  /** ÚNICA implementación de reverse para cualquier array int[] */
  private void reverse(int[] t,int a,int b){ while(a<b){ int tmp=t[a]; t[a]=t[b]; t[b]=tmp; a++; b--; } }

  private void reconcat(int[] t,int i,int j,int k,boolean rev1,boolean rev2){
    int[] s1 = Arrays.copyOfRange(t,i,j);
    int[] s2 = Arrays.copyOfRange(t,j,k);
    if (rev1) reverse(s1,0,s1.length-1);
    if (rev2) reverse(s2,0,s2.length-1);
    splice(t,i,k,concat(s1,s2));
  }
  private void reconcatSwap(int[] t,int i,int j,int k){
    int[] s1 = Arrays.copyOfRange(t,i,j);
    int[] s2 = Arrays.copyOfRange(t,j,k);
    splice(t,i,k,concat(s2,s1));
  }
  private int[] concat(int[] a,int[] b){
    int[] r = Arrays.copyOf(a, a.length + b.length);
    System.arraycopy(b, 0, r, a.length, b.length);
    return r;
  }
  private void splice(int[] t,int from,int to,int[] with){
    int len = to - from;
    if (with.length != len) throw new IllegalStateException("splice len mismatch");
    for (int x=0;x<len;x++) t[from + x] = with[x];
  }
}

