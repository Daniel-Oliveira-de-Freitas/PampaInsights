import { defineComponent } from 'vue';
import AkipChat from '@/components/akip-chat.vue';

export default defineComponent({
  components: { 'akip-chat': AkipChat },
  compatConfig: { MODE: 3 },
  name: 'akip-chat-page',
});
