package experiments.photographyDirector;

import static experiments.photographyDirector.Constants.*;
import experiments.photographyDirector.Occupant;
import experiments.photographyDirector.MiddleElement;
import experiments.photographyDirector.InputParser;

/**
 * Class that holds all necessary search-in-progress (or count-in-progress) state.
 * This handle search/count of only 1 direction: 
 * either we are considering P - A - B photographs 
 * OR we are considering B - A - P photographs where elements are listed in ascending order of their indexes in the input.
 */
public class SearchProcess {
    
    /**
     * The amount of already found artistic photographs.
     */
    public int currentCount = 0;
    
    /**
     * Current first element of the photo we are considering for artistic photographs.
     * Can be either photographer or a backdrop - depending on {@link #photographerFirst} flag.
     */
    public Occupant pb1 = null;
    
    /**
     * The first element of the photo we no longer need to store info about for THIS search - 
     * to be released into garbage collector when possible.
     */
    public Occupant lastFreedPb1 = null;
    
    /**
     * Current middle element of the photo we are considering for artistic photographs.
     * This one always will be an actor. We also accumulate information about potential tails in here,
     * so we don't need to calculate is multiple times.
     */
    public MiddleElement a2 = null;
    
    /**
     * The 1st actor element that has index bigger than or equal to the minimum required index of actor
     * relative to the current 1st element {@link #pb1} for the photograph to be artistic.
     */
    public MiddleElement aMin = null;
    
    /**
     * The actor element of the photo we no longer need to store info about for THIS search - 
     * to be released into garbage collector when possible.
     */
    public MiddleElement lastFreedA = null;
    
    /**
     * Current last element of the photo we are considering for artistic photographs.
     * Can be either photographer or a backdrop - depending on {@link #photographerFirst} flag.
     */
    public Occupant pb3 = null;
    
    /**
     * The last element of the photo we no longer need to store info about for THIS search - 
     * to be released into garbage collector when possible.
     */
    public Occupant lastFreedPb3 = null;
    
    /**
     * Determines type of the first (and last) element of the artistic photographs we are currently counting.
     * If {@code true} - it means 1st element will be a photographer and the last will be a backdrop.
     * Otherwise the order is the opposite (backdrop first, photographer - last)
     */
    public final boolean photographerFirst;
    
    /**
     * As we're counting photographs during the parsing without waiting for parsing to be completed - 
     * this state allows us to know which element we're missing and need to parse (if any) in order to proceed.
     * Possible values are {@code ON_PHOTOGRAPHER, ON_ACTOR, ON_BACKDROP, NOT_WAITING}
     */
    public Waiting obstacle;
    
    /**
     * Flag that indicates whether current artist element found is the first attempt for the current 1st element.
     */
    private boolean freshFirst = true;
    
    /**
     * Initializes the search providing necessary distance, order of photograph elements and the first element (hopefully)
     * @param x - the minimum distance between elements of artistic photographs
     * @param y - the maximum distance between elements of artistic photographs.
     * @param pFirst - flag that indicates whether the first elements are photographers in the photographs we will count.
     * @param first - the first element of the photograph to consider.
     */
    public SearchProcess (int x, int y, boolean pFirst, Occupant first) {
      if (0 >= x || 0 >= y || x > y) {
        throw new IllegalArgumentException("Need positive distance values where x <= y");
      }
      this.photographerFirst = pFirst;
      this.pb1 = first;
      if (null == this.pb1) {
        if (this.photographerFirst) {
          this.obstacle = Waiting.ON_PHOTOGRAPHER;
        } else {
          this.obstacle = Waiting.ON_BACKDROP;
        }
      } else {
        this.obstacle = Waiting.NOT_WAITING;
      }
    }
    
    /**
     * Attempts to used already parsed info to find an artistic photograph and update count.
     * It picks up where it left off based on the value of {@link #obstacle}, and goes on 
     * while there is enough already parsed info.
     * If the parser's current char does not correspond to this search process state (obstacle) 
     * then nothing happens.
     * @param parsedInfo - the information received from parsing.
     */
    public void proceed(InputParser parsedInfo) {
      // If we were waiting on something and the input isn't it - don't do anything
      if (Waiting.NOT_WAITING != this.obstacle) {
        if ((P == parsedInfo.currentChar && Waiting.ON_PHOTOGRAPHER != this.obstacle)
            || (A == parsedInfo.currentChar && Waiting.ON_ACTOR != this.obstacle)
            || (B == parsedInfo.currentChar && Waiting.ON_BACKDROP != this.obstacle)
            || (DOT == parsedInfo.currentChar)) {
          // we're waiting on something else - nothing to do
          return;
        }
      }
      
      // If we were waiting on something and this is it - find the right thing to do.
      if (Waiting.ON_PHOTOGRAPHER == this.obstacle && P == parsedInfo.currentChar) {
        if (this.photographerFirst) {
          this.countWhileCan(parsedInfo);
        } else {
          this.proceedWith3rd(parsedInfo);
          if (Waiting.NOT_WAITING == this.obstacle) {
            this.proceedWithActor(parsedInfo);
          }
          if (Waiting.NOT_WAITING == this.obstacle) {
            this.countWhileCan(parsedInfo);
          }
        }
      } else if (Waiting.ON_ACTOR == this.obstacle && A == parsedInfo.currentChar) {
        this.proceedWithActor(parsedInfo);
        if (Waiting.NOT_WAITING == this.obstacle) {
          this.countWhileCan(parsedInfo);
        }
      } else if (Waiting.ON_BACKDROP == this.obstacle && B == parsedInfo.currentChar) {
        if (this.photographerFirst) {
          this.proceedWith3rd(parsedInfo);
          if (Waiting.NOT_WAITING == this.obstacle) {
            this.proceedWithActor(parsedInfo);
          }
          if (Waiting.NOT_WAITING == this.obstacle) {
            this.countWhileCan(parsedInfo);
          }
        } else {
          this.countWhileCan(parsedInfo);
        }
      }
      
    }
    
    /**
     * This method starts a new artistic element search from the first (closest to the beginning)
     * element of the photo. 
     * @param parsedInfo - the information received from parsing.
     */
    public void countWhileCan(InputParser parsedInfo) {
      // Get 1st element for the photograph and calculated possible amount of artistic photographs with that 1st element.
      do {
        if (null == this.pb1
            || (Waiting.ON_PHOTOGRAPHER == this.obstacle && this.photographerFirst)
            || (Waiting.ON_BACKDROP == this.obstacle && !this.photographerFirst)) {
          // it's either first element EVER or we needed a new one.
          if (this.photographerFirst) {
            this.pb1 = parsedInfo.pListLast;
          } else {
            this.pb1 = parsedInfo.bListLast;
          }
        } else if (null == this.pb1.next) {
          // we need next element, but we reached end of list. 
          // Is there more?
          if (parsedInfo.isFinished()) {
            // there will be no more variations
            // free whatever we can.
            this.lastFreedPb1 = this.pb1;
            this.pb1 = null;
            this.aMin = null;
            this.a2 = null;
            this.lastFreedPb3 = this.pb3;
            this.pb3 = null;
            this.obstacle = null;
            return;
          }
          if (this.photographerFirst) {
            this.obstacle = Waiting.ON_PHOTOGRAPHER;
            return;
          } else {
            this.obstacle = Waiting.ON_BACKDROP;
            return;
          }
        } else {
          // we need next 1st element, and we have it - so move it.
          this.lastFreedPb1 = this.pb1;
          this.pb1 = this.pb1.next;
        }
        this.obstacle = Waiting.NOT_WAITING;
      
        // Now that we've got 1st element - get the second one
        // we should start at the minimum
        this.freshFirst = true;
        this.proceedWithActor(parsedInfo);
        // TODO consider premature return due to the obstacle
      } while (Waiting.NOT_WAITING == this.obstacle); // TODO forgot what's the second condition.
      // TODO finish
    }
    
    /**
     * This method tries to find all "artistic" actors for current 1st element of the photograph,
     * as well as calls the method that finds and counts artistic tails. It goes on while 
     * there's enough parsed info.
     * @param parsedInfo - the information received from parsing. 
     */
    public void proceedWithActor(InputParser parsedInfo) {
      do {
        // Get 2nd element for the photograph
        if (null == this.a2) {
          // try to get one - we might have parsed some already
          if (null == parsedInfo.aListFirst) {
            // none available - change the obstacle and done for now
            this.obstacle = Waiting.ON_ACTOR;
            return;
          }
          this.a2 = parsedInfo.aListFirst;
        } else if (Waiting.ON_ACTOR == this.obstacle) {
          // we were waiting on a new actor to be parsed - so take it
          // put the existing pointers to minimum qualified tails if they exist
          parsedInfo.aListLast.qualifiedBTail = this.a2.qualifiedBTail;
          parsedInfo.aListLast.qualifiedPTail = this.a2.qualifiedPTail;
          this.a2 = parsedInfo.aListLast;
        } else {
          // we already processed at least one actor for the current 1st element, so get the next one.
          if (null == this.a2.next) {
            // there isn't next actor yet - gotta wait
            this.obstacle = Waiting.ON_ACTOR;
            return;
          } else {
            this.a2 = (MiddleElement) this.a2.next;
          }
        }
        // we just got new actor element - at this moment we aren't waiting on anything.
        this.obstacle = Waiting.NOT_WAITING;
        // is this new element fit for artistic photograph or is its index too small or too big?
        int minAIdx = pb1.idx + parsedInfo.x;
        int maxAIdx = pb1.idx + parsedInfo.y;
        if (this.freshFirst || minAIdx > this.a2.idx) {
          // the index is too small, need next actor. Update min and last freed
          this.lastFreedA = this.aMin;
          this.aMin = a2;
          // release min tail if it's already calculated and different
          if (null != this.lastFreedA && this.photographerFirst && null != this.aMin.qualifiedBTail 
              && this.lastFreedA.qualifiedBTail != this.aMin.qualifiedBTail) {
            this.lastFreedPb3 = this.lastFreedA.qualifiedBTail;
          } else if (null != lastFreedA && !this.photographerFirst && null != this.aMin.qualifiedPTail
              && this.lastFreedA.qualifiedPTail != this.aMin.qualifiedPTail) {
            this.lastFreedPb3 = this.lastFreedA.qualifiedPTail;
          }
          if (this.freshFirst) {
            this.freshFirst = false;
          }
          continue;
        } else if (maxAIdx < this.a2.idx) {
          // the index is too big - we should wrap up photos with current first element 
          // and move to the next first element
          return;
        } else {
          // the index is right to satisfy artistic photo requirement - check all possible valid tails
          // do we have them already calculated?
          if (this.photographerFirst && UNDEFINED != this.a2.bTailCount) {
            this.currentCount += this.a2.bTailCount;
          } else if (!this.photographerFirst && UNDEFINED != this.a2.pTailCount) {
            this.currentCount += this.a2.pTailCount;
          } else {
            // we don't have correct tail type calculated for this actor - calculate it now
            proceedWith3rd(parsedInfo);
            // TODO consider premature return
          }
        }
      } while (Waiting.NOT_WAITING == this.obstacle); // TODO forgot what is second condition is
      //TODO finish
    }

    /**
     * This method attempts to find all "artistic tails" (3rd elements) for
     * the current actor and when it does it updates global count as well as the actor's count.
     * It goes on while there's enough of parsed Info.
     * @param parsedInfo - the information received from parsing.
     */
    public void proceedWith3rd(InputParser parsedInfo) {
      Waiting waitingForNext;
      Occupant firstInList;
      Occupant lastInList;
      boolean freshActor;
      Occupant minQualifiedTail;
      if (photographerFirst) {
        waitingForNext = Waiting.ON_BACKDROP;
        firstInList = parsedInfo.bListFirst;
        lastInList = parsedInfo.bListLast;
        freshActor = this.a2.freshPabActor;
        minQualifiedTail = this.a2.qualifiedBTail;
      } else {
        waitingForNext = Waiting.ON_PHOTOGRAPHER;
        firstInList = parsedInfo.pListFirst;
        lastInList = parsedInfo.pListLast;
        freshActor = this.a2.freshPabActor;
        minQualifiedTail = this.a2.qualifiedPTail;
      }
      
        
      do {
        if (null == this.pb3) {
          // this is first - try to get one, we might have parsed it already.
          if (null == firstInList) {
            // no luck - have to wait till parsed
            this.obstacle = waitingForNext;
            return;
          } else {
            this.pb3 = firstInList;
          }
        } else if (waitingForNext == this.obstacle) {
          // we were waiting on 3rd element to be parsed to continue - so get it now
          this.pb3 = lastInList;
        } else if (freshActor) {
          // we weren't waiting on anything, but it's a fresh actor, so start with min qualified for PAB
          this.pb3 = minQualifiedTail;
          if (photographerFirst) {
            this.a2.freshPabActor = false;
          } else {
            this.a2.freshBapActor = false;
          }
        } else {
          // we weren't waiting on anything, just going through already parsed things for already used actor - so 
          // is there next?
          if (null == this.pb3.next) {
            this.obstacle = waitingForNext;
            return;
          } else {
            this.pb3 = this.pb3.next;
          }
        }
        // now we got our new 3rd element of the photo. 
        this.obstacle = Waiting.NOT_WAITING;
        // is it fit to complete artistic photo or is its index too small or too big?
        if (this.a2.minTailIdx > this.pb3.idx) {
          // the index is too small - advance to the next if it exists?
          //advance at least to the current one
          if (this.photographerFirst) {
            // if it was null - check for aMin and last released 
             this.a2.qualifiedBTail = this.pb3;
             if (this.aMin == this.a2 && null != this.lastFreedA && this.lastFreedA.qualifiedBTail != this.pb3) {
               this.lastFreedPb3 = this.lastFreedA.qualifiedBTail;
             }
          } else {
            this.a2.qualifiedPTail = this.pb3;
            if (this.aMin == this.a2 && null != this.lastFreedA && this.lastFreedA.qualifiedPTail != this.pb3) {
              this.lastFreedPb3 = this.lastFreedA.qualifiedPTail;
            }
          }
        } else if (this.a2.maxTailIdx < this.pb3.idx) {
          //the index is too big - wrap up this actor and move to the next actor
          return;
        } else {
          // the index is just right - add this combination to count and this tail to A count.
          //TODO remove debug output
          System.out.println("FOUND ARTISTIC: " + this.pb1 + ", " + this.a2 + ", " + this.pb3);
          this.currentCount++;
          if (this.photographerFirst) {
            this.a2.bTailCount++;
          } else {
            this.a2.pTailCount++;
          }
        }
      } while (Waiting.NOT_WAITING == this.obstacle); // TODO forgot what's the second condition
      //TODO finish      
    }


}
