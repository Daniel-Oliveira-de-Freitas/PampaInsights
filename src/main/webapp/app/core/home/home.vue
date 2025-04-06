<template>
  <div class="home container-fluid p-0">
    <div class="" v-if="!authenticated">
      <div class="row bg-gray-primary p-5" style="border-radius: 25px 25px 0px 0px">
        <div class="col-md-6">
          <h2 class="display-5 mb-5">
            <span class="text-purple">{{ t$('home.subtitle.p1') }}</span>
            {{ t$('home.subtitle.p2') }}
            <span class="text-purple">{{ t$('home.subtitle.p3') }}</span>
          </h2>

          <div class="my-5">
            <h3>
              {{ t$('home.question1.p1') }}
              <span class="text-purple">{{ t$('home.question1.p2') }}</span>
              {{ t$('home.question1.p3') }}
            </h3>
            <p v-text="t$('home.res1')"></p>
          </div>
          <div class="my-5">
            <h3>
              {{ t$('home.question2.p1') }}
              <span class="text-purple">{{ t$('home.question2.p2') }}</span>
              {{ t$('home.question2.p3') }}
              <span class="text-purple">{{ t$('home.question2.p4') }}</span>
              {{ t$('home.question2.p5') }}
              <span class="text-purple">{{ t$('home.question2.p6') }}</span>
              {{ t$('home.question2.p7') }}
            </h3>
            <ul>
              <li v-text="t$('home.ul.li1')"></li>
              <li v-text="t$('home.ul.li2')"></li>
              <li v-text="t$('home.ul.li3')"></li>
            </ul>
          </div>
        </div>

        <div class="col-md-6 mx-auto d-flex align-items-center">
          <div class="mx-auto">
            <b-carousel
              id="carousel-1"
              v-model="slide"
              :interval="4000"
              controls
              indicators
              background="#44475A"
              img-width="80"
              img-height="40"
              style="text-shadow: 1px 1px 2px #333"
              @sliding-start="onSlideStart"
              @sliding-end="onSlideEnd"
            >
              <b-carousel-slide img-src="/content/images/example1.png"></b-carousel-slide>

              <b-carousel-slide img-src="/content/images/example2.png"> </b-carousel-slide>
            </b-carousel>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
