package net.entelijan.tryout;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import akka.actor.*;
import scala.concurrent.duration.FiniteDuration;

public class AkkaTryout {

	// private static final FiniteDuration INTERVAL =
	// FiniteDuration.create(7813, TimeUnit.MICROSECONDS);
	private static final FiniteDuration INTERVAL = FiniteDuration.create(300, TimeUnit.MILLISECONDS);

	static class MusicActor extends UntypedActor {

		private int msgCount = 0;
		Optional<Long> latestTime = Optional.empty();

		public static Props props() {
			return Props.create(MusicActor.class);
		}

		@Override
		public void onReceive(Object message) throws Throwable {
			msgCount++;
			if (message instanceof MusicEvent) {
				System.out.printf("Received music event %3d %n", msgCount);
			} else if (message instanceof TimeEvent) {
				TimeEvent te = (TimeEvent) message;
				if (latestTime.isPresent()) {
					long diff = te.time - latestTime.get();
					double distr = (diff - INTERVAL.toNanos()) / 1_000_000.0;
					System.out.printf("Received time event %5d %20d diff: %10d distr[ms]:%10.3f %n", msgCount, te.time,
							diff, distr);
					latestTime = Optional.of(te.time);
				} else {
					latestTime = Optional.of(te.time);
				}
			} else {
				unhandled(message);
			}

		}
	}

	static class MusicEvent {

	}

	static class TimeEvent {

		public final long time;

		public TimeEvent(long time) {
			this.time = time;
		}

	}

	public static void main(String[] args) {
		ActorSystem sys = ActorSystem.create("minim");

		ActorRef musicActor = sys.actorOf(MusicActor.props());

		FiniteDuration zero = FiniteDuration.create(0, TimeUnit.SECONDS);
		sys.scheduler().schedule(zero, INTERVAL,
				() -> musicActor.tell(new TimeEvent(System.nanoTime()), ActorRef.noSender()), sys.dispatcher());

		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				musicActor.tell(new MusicEvent(), musicActor);
			} else {
				musicActor.tell("HALLO", musicActor);
			}
			pause(400);
		}

		sys.terminate();
		System.out.println("terminated");

	}

	private static void pause(int timeInMilliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeInMilliseconds);
		} catch (InterruptedException e) {
			// Nothing to do here
		}
	}

}
