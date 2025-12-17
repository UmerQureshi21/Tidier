import { useEffect, useRef } from "react";

type CarouselProps = {
  images: string[];
  flowSpeed: number; // px per second
  carouselHeight: number;
  height: number;
  width: string;
};

export default function Carousel({
  images,
  flowSpeed,
  height,
  carouselHeight,
  width,
}: CarouselProps) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const slideRefs = useRef<HTMLDivElement[]>([]);
  const requestRef = useRef<number>(0);
  const lastTime = useRef<number>(0);
  const speed = flowSpeed;
  let images2: string[] = images; // images2 is the array that gets updated when you press left or right

  useEffect(() => {
    const step = (time: number) => {
      if (!lastTime.current) lastTime.current = time; // sets lastTime to current time if it's not set yet
      const delta = time - lastTime.current; // time since last frame
      lastTime.current = time;

      const moveBy = (speed * delta) / 1000;

      for (let slide of slideRefs.current) {
        const currentLeft = parseFloat(slide.style.left || "0");
        const newLeft = currentLeft - moveBy;
        slide.style.left = `${newLeft}px`;
      }

      // Recycle any slide that has exited left edge
      for (let slide of slideRefs.current) {
        const left = parseFloat(slide.style.left || "0");
        const width = slide.offsetWidth;

        if (left + width < 0) {
          const maxRight = Math.max(
            ...slideRefs.current.map(
              (s) => parseFloat(s.style.left || "0") + s.offsetWidth
            )
          );
          slide.style.left = `${maxRight}px`;
        }
      }

      requestRef.current = requestAnimationFrame(step);
    };

    requestRef.current = requestAnimationFrame(step);

    return () => {
      if (requestRef.current) cancelAnimationFrame(requestRef.current);
    };
  }, [speed, images2]);

  return (
    <div className="relative">
      <div
        ref={containerRef}
        className=" 
          relative overflow-hidden w-full flex
          "
        style={{ height: `${carouselHeight}px` }}
      >
        {images2.map((src, idx) => (
          <div
            key={idx}
            ref={(el) => {
              if (el) slideRefs.current[idx] = el;
            }}
            className="
              absolute top-0 rounded overflow-visible
              flex-shrink-0 transform transition duration-100 ease
              "
            style={{
              left: `${idx * height}px`,
              width,
              height: `${height}px`,
            }}
          >
            <img
              src={src}
              alt={"idk"}
              className="
                  w-full h-full object-cover transition duration-300 ease 
                  "
              draggable={false}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
