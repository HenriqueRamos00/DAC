import { useMask } from "@react-input/mask";
 
export function usePhoneMask() {
  return useMask({
    mask: "(__) _____-____",
    replacement: { _: /\d/ },
  });
}