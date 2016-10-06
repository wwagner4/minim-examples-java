package net.entelijan.tryout;

import java.util.concurrent.TimeUnit;

import akka.actor.*;
import scala.concurrent.duration.FiniteDuration;

public class AkkaTryout {

	static class MusicActor extends UntypedActor {

		private int msgCount = 0;

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
				System.out.printf("Received time event %30d %n", te.time);
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

		sys.scheduler().schedule(FiniteDuration.create(2, TimeUnit.SECONDS),
				FiniteDuration.create(100, TimeUnit.MILLISECONDS), new Runnable() {

					@Override
					public void run() {
						musicActor.tell(new TimeEvent(System.nanoTime()), ActorRef.noSender());
					}
				}, sys.dispatcher());

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
			Thread.sleep(timeInMilliseconds);
		} catch (InterruptedException e) {
			// Nothing to do here
		}
	}

}
