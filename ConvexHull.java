import java.util.*;
import java.util.concurrent.CyclicBarrier;

class ConvexHull {

      int n, seed;
      int[] x, y;
      int MAX_X, MAX_Y, MIN_X;

      IntList points;
      IntList allConvexHulls;
      CyclicBarrier cb;

      ConvexHull(int n, int seed) {
          this.n = n;
          this.seed = seed;
          x = new int[n];
          y = new int[n];
          allConvexHulls = new IntList();

          NPunkter17 np = new NPunkter17(n, seed);
          np.fyllArrayer(x, y);
          points = np.lagIntList();
      }

      class Worker implements Runnable {
          int ind, start, end, localMax_X, localMin_X, localMAX_Y;
          IntList localPoints;

          Worker(int ind, int start, int end) {
              this.ind = ind;
              this.start = start;
              this.end = end;
              localPoints = new IntList();
              localMax_X = start;
              localMin_X = start;
              localMAX_Y = start;
          }

          public void run() {
              for (int i = start; i < end; i++) {
                  if (x[i] > x[localMax_X]) {
                      localMax_X = i;
                  }
                  else if (x[i] < x[localMin_X]) {
                      localMin_X = i;
                  }

                  if (y[i] > y[localMAX_Y]) {
                      localMAX_Y = i;
                  }
                  localPoints.add(points.get(i));
              }

              if(localPoints.size() > 0) {
                  IntList localConvexHull = new IntList();

                  localConvexHull.add(localMax_X);
                  findPointsToLeft(localMin_X, localMax_X, localPoints, localConvexHull);
                  if(localMax_X != localMin_X) {
                      localConvexHull.add(localMin_X);
                  }
                  findPointsToLeft(localMax_X, localMin_X, localPoints, localConvexHull);

                  addConvexHull(localConvexHull);
              }
          }
      }

      synchronized void addConvexHull(IntList hull) {
          allConvexHulls.append(hull);
      }

      public static void main(String[] args) {
          int n, seed;

          try {
            n = Integer.parseInt(args[0]);
            seed = Integer.parseInt(args[1]);

          } catch (Exception e) {
            System.out.println("Correct usage is: java ConvexHull <n> <seed> ");
            return;

          }

          ConvexHull ch = new ConvexHull(n, seed);

          double[] times = new double[7];

          IntList convexHull = ch.quickHull(ch.points);

          // Run sequential version 7 times
          for(int i = 0; i < 7; i++) {
              ch = new ConvexHull(n, seed);
              long time = System.nanoTime();
              convexHull = ch.quickHull(ch.points);
              times[i] = (System.nanoTime() - time) / 1000000.0;
          }
          double timeSeq = ch.median(times);
          System.out.println("Runtime sequential (ms) " + timeSeq);

          // Run paralell version 7 times
          for(int i = 0; i < 7; i++) {
              ch = new ConvexHull(n, seed);
              long time = System.nanoTime();
              convexHull = ch.quickHullPara();
              times[i] = (System.nanoTime() - time) / 1000000.0;
          }
          double timePar = ch.median(times);
          System.out.println("Runtime parllel (ms) " + timePar);

          System.out.println("Speedup: " + timeSeq / timePar);


          Oblig5Precode op = new Oblig5Precode(ch, convexHull);
          if(n < 10000) {
            op.drawGraph();
          }
          op.writeHullPoints();
      }

      //sequential solution
      IntList quickHull(IntList points) {
          for (int i = 0; i < points.size(); i++) {
              int p = points.get(i);
              if (x[p] > x[MAX_X]) {
                  MAX_X = p;
              }
              else if (x[p] < x[MIN_X]) {
                  MIN_X = p;
              }

                if (y[p] > y[MAX_Y])
                    MAX_Y = p;
          }

          IntList convexHull = new IntList();

          convexHull.add(MAX_X);
          findPointsToLeft(MIN_X, MAX_X, points, convexHull);
          convexHull.add(MIN_X);
          findPointsToLeft(MAX_X, MIN_X, points, convexHull);

          return convexHull;

      }

      //Parallel solution
      IntList quickHullPara() {
          int numOfThreads = Runtime.getRuntime().availableProcessors() * 5;
          Thread[] threads = new Thread[numOfThreads];
          cb = new CyclicBarrier(numOfThreads);

          int numOfPoints = n / numOfThreads;
          for (int i = 0; i < numOfThreads - 1; i++) {
            threads[i] = new Thread(new Worker(i, i * numOfPoints, (i + 1) * numOfPoints));
          }
          threads[numOfThreads - 1] = new Thread(new Worker(numOfThreads - 1, (numOfThreads - 1) * numOfPoints, n));

          for (Thread t : threads) t.start();

          try {
            for (Thread t : threads) t.join();
          } catch(Exception e) {
            e.printStackTrace();
          }

          return quickHull(allConvexHulls);
      }

      // Recursive method
      void findPointsToLeft(int point1, int point2, IntList allPoints, IntList convexHull) {

          int a = y[point1] - y[point2];
          int b = x[point2] - x[point1];
          int c = (y[point2] * x[point1]) - (y[point1] * x[point2]);

          int maxDistance = 0;
          int maxPoint = -1;

          IntList pointsToLeft = new IntList();
          IntList sameLine = new IntList();

          for (int i = 0; i < allPoints.size(); i++) {
              int p = allPoints.get(i);
              int d = a * x[p] + b * y[p] + c;

              if (d > 0) {
                  pointsToLeft.add(p);
                  if (d > maxDistance) {
                      maxDistance = d;
                      maxPoint = p;
                  }
              }
              if(d == 0 && p != point1 && p != point2) {
                  sameLine.add(p);
              }
          }

          if (maxPoint >= 0) {
              findPointsToLeft(maxPoint, point2, pointsToLeft, convexHull);
              convexHull.add(maxPoint);
              findPointsToLeft(point1, maxPoint, pointsToLeft, convexHull);
          }
          else {
              for(int i = 0; i < sameLine.size(); i++) {
                  convexHull.add(sameLine.get(i));
              }
          }

      }

      double median(double[] t) {
          Arrays.sort(t);
          return t[3];
      }

}
