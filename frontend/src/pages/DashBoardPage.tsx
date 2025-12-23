export default function () {
  let preSignedURL1 =
    "https://tidier.s3.amazonaws.com/test/ny-streets.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251222T171453Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=AKIAZTYAHU7SPWOAEJ7I%2F20251222%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=c6e83fd0298912e1a12abfae4daf0304eca3da251eaaac7b21df1a0fa066ef0e";
  let preSignedURL2 =
    "https://tidier.s3.amazonaws.com/test/ny-helicopter.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251221T054942Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=AKIAZTYAHU7SPWOAEJ7I%2F20251221%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=05ce6ec2386a7d86e55c12629e542855cc85557ad7f31aa5cf2c4970305d6ed7";
  let preSignedURL3 =
    "https://tidier.s3.amazonaws.com/test/ny-streets.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251221T054942Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=AKIAZTYAHU7SPWOAEJ7I%2F20251221%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=25299b2ac7f4cfb69e72f7c4470e5563213de606c9d1230c1189885177b60c0e";
  let preSignedURL4 =
    "https://tidier.s3.amazonaws.com/test/staten-island-streets.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251221T054942Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=AKIAZTYAHU7SPWOAEJ7I%2F20251221%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=0a9b64e36ddee2a5f929c9b00238304b4a91fca57a62b1a332dc61ad429211e2";

  return (
    <div className="bg-red-500 min-h-screen w-full">
      DASHBOARD PAGE
      <video src={preSignedURL1}  controls></video>
      <video src={preSignedURL2}  controls></video>
      <video src={preSignedURL3}  controls></video>
      <video src={preSignedURL4}  controls></video>
    </div>
  );
}
